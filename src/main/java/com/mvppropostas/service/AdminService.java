package com.mvppropostas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.client.MercadoPagoClient;
import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.common.exception.ResourceNotFoundException;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.domain.entity.Subscription;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.response.AdminAccountResponse;
import com.mvppropostas.dto.response.AdminAccountsPageResponse;
import com.mvppropostas.dto.response.AdminMetricsResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.repository.SubscriptionRepository;
import com.mvppropostas.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private static final Set<Integer> PERIOD_DAYS = Set.of(0, 7, 30, 90);
  private static final String AUTHORIZED_STATUS = "authorized";
  private static final Map<String, String> ACCOUNT_SORTS =
      Map.of(
          "name", "name",
          "email", "email",
          "plan", "plan",
          "createdAt", "createdAt",
          "active", "active");

  private final AdminAccessService adminAccessService;
  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final ProposalRepository proposalRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final MercadoPagoProperties mercadoPagoProperties;
  private final MercadoPagoClient mercadoPagoClient;

  @Transactional(readOnly = true)
  public AdminMetricsResponse getMetrics(int periodDays) {
    adminAccessService.requireAdmin();
    int days = PERIOD_DAYS.contains(periodDays) ? periodDays : 30;
    boolean allTime = days == 0;

    long totalUsers = userRepository.count();
    long freeUsers = userRepository.countByPlan(UserPlan.FREE);
    long proUsers = userRepository.countByPlan(UserPlan.PRO);
    BigDecimal mrr = MercadoPagoProperties.PRO_AMOUNT.multiply(BigDecimal.valueOf(proUsers));

    LocalDateTime since = allTime ? null : LocalDateTime.now().minusDays(days);

    long activeUsers =
        allTime
            ? userRepository.countByLastLoginIsNotNull()
            : userRepository.countByLastLoginGreaterThanEqual(since);
    long newUsers =
        allTime ? totalUsers : userRepository.countByCreatedAtGreaterThanEqual(since);
    long subscriptions =
        allTime
            ? subscriptionRepository.count()
            : subscriptionRepository.countByCreatedAtGreaterThanEqual(since);
    long cancellations =
        allTime
            ? subscriptionRepository.countByStatusNotIgnoreCase(AUTHORIZED_STATUS)
            : subscriptionRepository.countByUpdatedAtGreaterThanEqualAndStatusNotIgnoreCase(
                since, AUTHORIZED_STATUS);

    return new AdminMetricsResponse(
        days,
        totalUsers,
        activeUsers,
        freeUsers,
        proUsers,
        newUsers,
        subscriptions,
        cancellations,
        mrr,
        MercadoPagoProperties.PRO_CURRENCY,
        proposalRepository.countByDeletedAtIsNull(),
        clientRepository.count());
  }

  @Transactional(readOnly = true)
  public AdminAccountsPageResponse listAccounts(
      String query, UserPlan plan, int page, int size, String sort, String direction) {
    adminAccessService.requireAdmin();
    int safePage = Math.max(page, 0);
    int safeSize = size < 1 ? 20 : Math.min(size, 100);
    String normalizedQuery = query == null || query.isBlank() ? "" : query.trim();
    String sortProperty = ACCOUNT_SORTS.getOrDefault(sort == null ? "" : sort, "createdAt");
    Sort.Direction sortDirection =
        "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;

    Page<User> result =
        userRepository.searchAccounts(
            normalizedQuery,
            plan,
            PageRequest.of(safePage, safeSize, Sort.by(sortDirection, sortProperty)));

    return new AdminAccountsPageResponse(
        result.getContent().stream().map(AdminService::toAccount).toList(),
        result.getNumber(),
        result.getSize(),
        result.getTotalPages(),
        result.getTotalElements());
  }

  @Transactional
  public AdminAccountResponse changePlan(UUID accountId, UserPlan plan) {
    User actor = adminAccessService.requireAdmin();
    User target = requireAccount(accountId);
    if (target.getPlan() != plan) {
      if (plan == UserPlan.FREE) {
        cancelBilling(target);
      }
      target.setPlan(plan);
      target.setUpdatedAt(LocalDateTime.now());
      userRepository.save(target);
      log.info("Admin {} alterou o plano de {} para {}", actor.getEmail(), target.getEmail(), plan);
    }
    return toAccount(target);
  }

  @Transactional
  public AdminAccountResponse cancelAccount(UUID accountId) {
    User actor = adminAccessService.requireAdmin();
    User target = requireAccount(accountId);
    if (actor.getId().equals(target.getId())) {
      throw new BusinessException("Não é possível cancelar a própria conta pelo painel.");
    }
    if (!target.isActive()) {
      return toAccount(target);
    }
    cancelBilling(target);
    target.setActive(false);
    target.setPlan(UserPlan.FREE);
    target.setUpdatedAt(LocalDateTime.now());
    userRepository.save(target);
    log.info("Admin {} cancelou a conta {}", actor.getEmail(), target.getEmail());
    return toAccount(target);
  }

  private User requireAccount(UUID accountId) {
    return userRepository
        .findById(accountId)
        .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
  }

  private void cancelBilling(User user) {
    subscriptionRepository
        .findTopByUserIdOrderByCreatedAtDesc(user.getId())
        .ifPresent(
            subscription -> {
              String status = subscription.getStatus() == null ? "" : subscription.getStatus().toLowerCase(Locale.ROOT);
              if ("cancelled".equals(status)) {
                return;
              }
              if (mercadoPagoProperties.isConfigured() && subscription.getMpPreapprovalId() != null) {
                try {
                  mercadoPagoClient.cancelPreapproval(subscription.getMpPreapprovalId());
                } catch (RuntimeException exception) {
                  log.warn(
                      "Falha ao cancelar preapproval {} no Mercado Pago: {}",
                      subscription.getMpPreapprovalId(),
                      exception.getMessage());
                }
              }
              subscription.setStatus("cancelled");
              subscription.setUpdatedAt(LocalDateTime.now());
              subscriptionRepository.save(subscription);
            });
  }

  private static AdminAccountResponse toAccount(User user) {
    return new AdminAccountResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPlan(),
        user.isActive(),
        user.getCreatedAt());
  }
}
