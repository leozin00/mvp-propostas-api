package com.mvppropostas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mvppropostas.common.exception.PlanLimitException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.response.PlanUsageResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanLimitService {

  public static final int FREE_CLIENT_LIMIT = 10;
  public static final int FREE_PROPOSAL_MONTHLY_LIMIT = 5;

  private final CurrentUserProvider currentUserProvider;
  private final ClientRepository clientRepository;
  private final ProposalRepository proposalRepository;

  public PlanUsageResponse currentUsage() {
    return usageOf(currentUserProvider.getCurrentUser(), LocalDateTime.now());
  }

  public void assertCanCreateClient() {
    User user = currentUserProvider.getCurrentUser();
    if (user.getPlan() != UserPlan.FREE) {
      return;
    }
    if (clientRepository.countByUserId(user.getId()) >= FREE_CLIENT_LIMIT) {
      throw new PlanLimitException(
          PlanLimitException.CLIENTS,
          "Você atingiu o limite de "
              + FREE_CLIENT_LIMIT
              + " clientes do plano gratuito. Assine o Pro para cadastrar clientes ilimitados.");
    }
  }

  public void assertCanCreateProposal() {
    User user = currentUserProvider.getCurrentUser();
    if (user.getPlan() != UserPlan.FREE) {
      return;
    }
    LocalDateTime cycleStart = cycleStart(user.getCreatedAt(), LocalDateTime.now());
    if (proposalRepository.countByUserIdAndCreatedAtGreaterThanEqual(user.getId(), cycleStart)
        >= FREE_PROPOSAL_MONTHLY_LIMIT) {
      throw new PlanLimitException(
          PlanLimitException.PROPOSALS,
          "Você atingiu o limite de "
              + FREE_PROPOSAL_MONTHLY_LIMIT
              + " orçamentos neste ciclo mensal do plano gratuito. Assine o Pro para criar orçamentos ilimitados.");
    }
  }

  PlanUsageResponse usageOf(User user, LocalDateTime now) {
    UUID userId = user.getId();
    int clientsUsed = Math.toIntExact(clientRepository.countByUserId(userId));
    int proposalsUsed =
        Math.toIntExact(
            proposalRepository.countByUserIdAndCreatedAtGreaterThanEqual(
                userId, cycleStart(user.getCreatedAt(), now)));

    if (user.getPlan() != UserPlan.FREE) {
      return new PlanUsageResponse(clientsUsed, null, false, proposalsUsed, null, false);
    }

    return new PlanUsageResponse(
        clientsUsed,
        FREE_CLIENT_LIMIT,
        clientsUsed >= FREE_CLIENT_LIMIT,
        proposalsUsed,
        FREE_PROPOSAL_MONTHLY_LIMIT,
        proposalsUsed >= FREE_PROPOSAL_MONTHLY_LIMIT);
  }

  /**
   * Monthly cycle anchored on the account creation day, as defined in {@code docs/monetization.md}.
   * Example: created on the 15th → each cycle runs from the 15th through the day before the next
   * 15th. Days that do not exist in a month (e.g. 31) clamp to the last day of that month.
   */
  static LocalDateTime cycleStart(LocalDateTime accountCreated, LocalDateTime now) {
    LocalDate created = accountCreated.toLocalDate();
    LocalDate today = now.toLocalDate();
    LocalDate candidate = clampToMonth(today, created.getDayOfMonth());
    if (candidate.isAfter(today)) {
      candidate = clampToMonth(today.minusMonths(1), created.getDayOfMonth());
    }
    return candidate.atStartOfDay();
  }

  private static LocalDate clampToMonth(LocalDate month, int dayOfMonth) {
    return month.withDayOfMonth(Math.min(dayOfMonth, month.lengthOfMonth()));
  }
}
