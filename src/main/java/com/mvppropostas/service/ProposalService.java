package com.mvppropostas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.common.exception.ResourceNotFoundException;
import com.mvppropostas.domain.entity.Client;
import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.entity.ProposalItem;
import com.mvppropostas.domain.enums.ProposalStatus;
import com.mvppropostas.dto.request.ProposalItemRequest;
import com.mvppropostas.dto.request.ProposalRequest;
import com.mvppropostas.dto.response.ProposalItemResponse;
import com.mvppropostas.dto.response.ProposalResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProposalService {

  private static final int MONEY_SCALE = 2;

  private final ProposalRepository proposalRepository;
  private final ClientRepository clientRepository;
  private final CurrentUserProvider currentUserProvider;
  private final PlanLimitService planLimitService;
  private final ProposalPdfService proposalPdfService;

  @Transactional(readOnly = true)
  public List<ProposalResponse> list(UUID clientId) {
    UUID userId = currentUserProvider.getCurrentUserId();
    List<Proposal> proposals =
        clientId == null
            ? proposalRepository.findVisibleByUserId(userId)
            : proposalRepository.findVisibleByUserIdAndClientId(userId, clientId);
    return proposals.stream().map(proposal -> toResponse(proposal, false)).toList();
  }

  @Transactional(readOnly = true)
  public ProposalResponse getById(UUID id) {
    return toResponse(requireProposal(id), true);
  }

  @Transactional(readOnly = true)
  public byte[] exportPdf(UUID id) {
    return proposalPdfService.render(requireProposal(id), currentUserProvider.getCurrentUser());
  }

  @Transactional
  public ProposalResponse create(ProposalRequest request) {
    planLimitService.assertCanCreateProposal();
    UUID userId = currentUserProvider.getCurrentUserId();
    Client client = requireOwnedClient(request.clientId(), userId);
    LocalDateTime now = LocalDateTime.now();

    Proposal proposal = new Proposal();
    proposal.setId(UUID.randomUUID());
    proposal.setUserId(userId);
    proposal.setClient(client);
    proposal.setStatus(ProposalStatus.DRAFT);
    proposal.setCreatedAt(now);
    proposal.setUpdatedAt(now);
    applyContent(proposal, request);
    return toResponse(proposalRepository.save(proposal), true);
  }

  @Transactional
  public ProposalResponse update(UUID id, ProposalRequest request) {
    Proposal proposal = requireProposal(id);
    ensureDraft(proposal);
    proposal.setClient(requireOwnedClient(request.clientId(), proposal.getUserId()));
    applyContent(proposal, request);
    proposal.setUpdatedAt(LocalDateTime.now());
    return toResponse(proposalRepository.save(proposal), true);
  }

  @Transactional
  public void delete(UUID id) {
    Proposal proposal = requireProposal(id);
    LocalDateTime now = LocalDateTime.now();
    proposal.setDeletedAt(now);
    proposal.setUpdatedAt(now);
    proposalRepository.save(proposal);
  }

  @Transactional
  public ProposalResponse publish(UUID id) {
    Proposal proposal = requireProposal(id);
    ensureDraft(proposal);
    if (proposal.getItems().isEmpty()) {
      throw new BusinessException("Inclua pelo menos um item antes de publicar.");
    }

    LocalDateTime now = LocalDateTime.now();
    proposal.setStatus(ProposalStatus.SENT);
    proposal.setSentAt(now);
    proposal.setUpdatedAt(now);
    if (proposal.getPublicToken() == null || proposal.getPublicToken().isBlank()) {
      proposal.setPublicToken(UUID.randomUUID().toString().replace("-", ""));
    }
    return toResponse(proposalRepository.save(proposal), true);
  }

  @Transactional
  public ProposalResponse duplicate(UUID id) {
    planLimitService.assertCanCreateProposal();
    Proposal source = requireProposal(id);
    LocalDateTime now = LocalDateTime.now();

    Proposal copy = new Proposal();
    copy.setId(UUID.randomUUID());
    copy.setUserId(source.getUserId());
    copy.setClient(source.getClient());
    copy.setTitle(source.getTitle() + " (cópia)");
    copy.setDescription(source.getDescription());
    copy.setStatus(ProposalStatus.DRAFT);
    copy.setValidUntil(source.getValidUntil());
    copy.setSubtotal(source.getSubtotal());
    copy.setDiscount(source.getDiscount());
    copy.setTotal(source.getTotal());
    copy.setCreatedAt(now);
    copy.setUpdatedAt(now);

    for (ProposalItem sourceItem : source.getItems()) {
      ProposalItem item = new ProposalItem();
      item.setId(UUID.randomUUID());
      item.setProposal(copy);
      item.setDescription(sourceItem.getDescription());
      item.setQuantity(sourceItem.getQuantity());
      item.setUnitPrice(sourceItem.getUnitPrice());
      item.setTotal(sourceItem.getTotal());
      copy.getItems().add(item);
    }

    return toResponse(proposalRepository.save(copy), true);
  }

  private void applyContent(Proposal proposal, ProposalRequest request) {
    proposal.setTitle(request.title().trim());
    proposal.setDescription(blankToNull(request.description()));
    proposal.setValidUntil(request.validUntil());
    replaceItems(proposal, request.items());
    recalculate(proposal, request.discount());
  }

  private void replaceItems(Proposal proposal, List<ProposalItemRequest> itemRequests) {
    proposal.getItems().clear();
    for (ProposalItemRequest itemRequest : itemRequests) {
      ProposalItem item = new ProposalItem();
      item.setId(UUID.randomUUID());
      item.setProposal(proposal);
      item.setDescription(itemRequest.description().trim());
      item.setQuantity(scale(itemRequest.quantity()));
      item.setUnitPrice(scale(itemRequest.unitPrice()));
      item.setTotal(scale(item.getQuantity().multiply(item.getUnitPrice())));
      proposal.getItems().add(item);
    }
  }

  private void recalculate(Proposal proposal, BigDecimal discountInput) {
    BigDecimal subtotal =
        proposal.getItems().stream()
            .map(ProposalItem::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal discount = scale(discountInput == null ? BigDecimal.ZERO : discountInput);
    if (discount.compareTo(subtotal) > 0) {
      throw new BusinessException("O desconto não pode ser maior que o subtotal.");
    }
    proposal.setSubtotal(scale(subtotal));
    proposal.setDiscount(discount);
    proposal.setTotal(scale(subtotal.subtract(discount)));
  }

  private Proposal requireProposal(UUID id) {
    return proposalRepository
        .findVisibleByIdAndUserId(id, currentUserProvider.getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada."));
  }

  private Client requireOwnedClient(UUID clientId, UUID userId) {
    return clientRepository
        .findByIdAndUserId(clientId, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
  }

  private static void ensureDraft(Proposal proposal) {
    if (proposal.getStatus() != ProposalStatus.DRAFT) {
      throw new BusinessException("Somente rascunhos podem ser alterados.");
    }
  }

  private ProposalResponse toResponse(Proposal proposal, boolean includeItems) {
    List<ProposalItemResponse> items =
        includeItems
            ? proposal.getItems().stream().map(this::toItemResponse).toList()
            : List.of();

    return new ProposalResponse(
        proposal.getId(),
        proposal.getClientId(),
        proposal.getClient().getName(),
        proposal.getClient().getPhone(),
        proposal.getTitle(),
        proposal.getDescription(),
        proposal.getStatus(),
        proposal.getValidUntil(),
        proposal.getSubtotal(),
        proposal.getDiscount(),
        proposal.getTotal(),
        proposal.getPublicToken(),
        proposal.getCreatedAt().toLocalDate(),
        items);
  }

  private ProposalItemResponse toItemResponse(ProposalItem item) {
    return new ProposalItemResponse(
        item.getId(),
        item.getDescription(),
        item.getQuantity(),
        item.getUnitPrice(),
        item.getTotal());
  }

  private static BigDecimal scale(BigDecimal value) {
    return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
