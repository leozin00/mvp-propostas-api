package com.mvppropostas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.common.exception.ResourceNotFoundException;
import com.mvppropostas.domain.entity.Client;
import com.mvppropostas.dto.request.ClientRequest;
import com.mvppropostas.dto.response.ClientResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final ProposalRepository proposalRepository;
  private final CurrentUserProvider currentUserProvider;
  private final PlanLimitService planLimitService;

  @Transactional(readOnly = true)
  public List<ClientResponse> list() {
    UUID userId = currentUserProvider.getCurrentUserId();
    Map<UUID, Long> counts =
        proposalRepository.countVisibleByClientForUser(userId).stream()
            .collect(Collectors.toMap(row -> (UUID) row[0], row -> (Long) row[1]));

    return clientRepository.findByUserIdOrderByNameAsc(userId).stream()
        .map(client -> toResponse(client, counts.getOrDefault(client.getId(), 0L)))
        .toList();
  }

  @Transactional(readOnly = true)
  public ClientResponse getById(UUID id) {
    Client client = requireOwnedClient(id);
    return toResponse(client, proposalRepository.countByClientIdAndDeletedAtIsNull(client.getId()));
  }

  @Transactional
  public ClientResponse create(ClientRequest request) {
    planLimitService.assertCanCreateClient();
    LocalDateTime now = LocalDateTime.now();
    Client client = new Client();
    client.setId(UUID.randomUUID());
    client.setUserId(currentUserProvider.getCurrentUserId());
    client.setCreatedAt(now);
    client.setUpdatedAt(now);
    apply(client, request);
    return toResponse(clientRepository.save(client), 0);
  }

  @Transactional
  public ClientResponse update(UUID id, ClientRequest request) {
    Client client = requireOwnedClient(id);
    apply(client, request);
    client.setUpdatedAt(LocalDateTime.now());
    return toResponse(
        clientRepository.save(client),
        proposalRepository.countByClientIdAndDeletedAtIsNull(client.getId()));
  }

  @Transactional
  public void delete(UUID id) {
    Client client = requireOwnedClient(id);
    if (proposalRepository.existsByClientIdAndDeletedAtIsNull(client.getId())) {
      throw new BusinessException("Não é possível excluir um cliente que possui propostas.");
    }
    clientRepository.delete(client);
  }

  private Client requireOwnedClient(UUID id) {
    return clientRepository
        .findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));
  }

  private void apply(Client client, ClientRequest request) {
    client.setName(request.name().trim());
    client.setEmail(blankToNull(request.email()));
    client.setPhone(blankToNull(request.phone()));
    client.setDocument(blankToNull(request.document()));
    client.setCompanyName(blankToNull(request.companyName()));
  }

  private ClientResponse toResponse(Client client, long proposalsCount) {
    return new ClientResponse(
        client.getId(),
        client.getName(),
        client.getEmail(),
        client.getPhone(),
        client.getDocument(),
        client.getCompanyName(),
        client.getCreatedAt().toLocalDate(),
        proposalsCount);
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
