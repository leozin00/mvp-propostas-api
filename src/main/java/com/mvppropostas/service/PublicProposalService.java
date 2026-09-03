package com.mvppropostas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.common.exception.ResourceNotFoundException;
import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.entity.ProposalItem;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.ProposalStatus;
import com.mvppropostas.dto.response.ProposalItemResponse;
import com.mvppropostas.dto.response.PublicProposalResponse;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicProposalService {

  private final ProposalRepository proposalRepository;
  private final UserRepository userRepository;
  private final ProposalPdfService proposalPdfService;

  @Transactional
  public PublicProposalResponse getByToken(String token) {
    Proposal proposal = requirePublished(token);
    expireIfNeeded(proposal);
    markViewedIfSent(proposal);
    return toResponse(proposal, requireIssuer(proposal));
  }

  @Transactional
  public byte[] exportPdf(String token) {
    Proposal proposal = requirePublished(token);
    expireIfNeeded(proposal);
    markViewedIfSent(proposal);
    return proposalPdfService.render(proposal, requireIssuer(proposal));
  }

  @Transactional
  public PublicProposalResponse approve(String token) {
    return decide(token, ProposalStatus.APPROVED);
  }

  @Transactional
  public PublicProposalResponse reject(String token) {
    return decide(token, ProposalStatus.REJECTED);
  }

  private PublicProposalResponse decide(String token, ProposalStatus target) {
    Proposal proposal = requirePublished(token);
    expireIfNeeded(proposal);
    ensureRespondable(proposal);

    LocalDateTime now = LocalDateTime.now();
    proposal.setStatus(target);
    proposal.setUpdatedAt(now);
    if (target == ProposalStatus.APPROVED) {
      proposal.setApprovedAt(now);
    } else {
      proposal.setRejectedAt(now);
    }
    proposalRepository.save(proposal);
    return toResponse(proposal, requireIssuer(proposal));
  }

  private Proposal requirePublished(String token) {
    return proposalRepository
        .findPublishedByPublicToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada."));
  }

  private User requireIssuer(Proposal proposal) {
    return userRepository
        .findById(proposal.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada."));
  }

  private void markViewedIfSent(Proposal proposal) {
    if (proposal.getStatus() != ProposalStatus.SENT) {
      return;
    }
    LocalDateTime now = LocalDateTime.now();
    proposal.setStatus(ProposalStatus.VIEWED);
    proposal.setViewedAt(now);
    proposal.setUpdatedAt(now);
    proposalRepository.save(proposal);
  }

  private void expireIfNeeded(Proposal proposal) {
    ProposalStatus status = proposal.getStatus();
    if (status != ProposalStatus.SENT && status != ProposalStatus.VIEWED) {
      return;
    }
    if (!LocalDate.now().isAfter(proposal.getValidUntil())) {
      return;
    }
    proposal.setStatus(ProposalStatus.EXPIRED);
    proposal.setUpdatedAt(LocalDateTime.now());
    proposalRepository.save(proposal);
  }

  private static void ensureRespondable(Proposal proposal) {
    ProposalStatus status = proposal.getStatus();
    if (status == ProposalStatus.EXPIRED) {
      throw new BusinessException("Esta proposta já expirou e não pode ser respondida.");
    }
    if (status != ProposalStatus.SENT && status != ProposalStatus.VIEWED) {
      throw new BusinessException("Esta proposta já foi respondida.");
    }
  }

  private PublicProposalResponse toResponse(Proposal proposal, User issuer) {
    List<ProposalItemResponse> items =
        proposal.getItems().stream().map(this::toItemResponse).toList();

    String issuerName =
        issuer.getCompanyName() != null && !issuer.getCompanyName().isBlank()
            ? issuer.getCompanyName().trim()
            : issuer.getName();

    String referenceCode = proposal.getId().toString().replace("-", "").substring(0, 8).toUpperCase();

    return new PublicProposalResponse(
        proposal.getTitle(),
        proposal.getDescription(),
        proposal.getClient().getName(),
        proposal.getStatus(),
        proposal.getValidUntil(),
        proposal.getSubtotal(),
        proposal.getDiscount(),
        proposal.getTotal(),
        issuerName,
        referenceCode,
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
}
