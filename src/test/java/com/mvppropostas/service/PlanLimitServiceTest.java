package com.mvppropostas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvppropostas.common.exception.PlanLimitException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.response.PlanUsageResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class PlanLimitServiceTest {

  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private ClientRepository clientRepository;
  @Mock private ProposalRepository proposalRepository;

  @InjectMocks private PlanLimitService planLimitService;

  @Test
  void cycleStartUsesAccountAnniversaryInCurrentMonth() {
    LocalDateTime created = LocalDateTime.of(2026, 3, 15, 10, 0);
    LocalDateTime now = LocalDateTime.of(2026, 8, 26, 12, 0);

    assertThat(PlanLimitService.cycleStart(created, now)).isEqualTo(LocalDateTime.of(2026, 8, 15, 0, 0));
  }

  @Test
  void cycleStartFallsBackToPreviousMonthBeforeAnniversary() {
    LocalDateTime created = LocalDateTime.of(2026, 3, 15, 10, 0);
    LocalDateTime now = LocalDateTime.of(2026, 8, 10, 12, 0);

    assertThat(PlanLimitService.cycleStart(created, now)).isEqualTo(LocalDateTime.of(2026, 7, 15, 0, 0));
  }

  @Test
  void cycleStartClampsMissingDays() {
    LocalDateTime created = LocalDateTime.of(2026, 1, 31, 8, 0);
    LocalDateTime now = LocalDateTime.of(2026, 2, 10, 9, 0);

    assertThat(PlanLimitService.cycleStart(created, now)).isEqualTo(LocalDateTime.of(2026, 1, 31, 0, 0));
  }

  @Test
  void freeUserCannotCreateEleventhClient() {
    User user = freeUser();
    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(clientRepository.countByUserId(user.getId())).thenReturn(10L);

    assertThatThrownBy(() -> planLimitService.assertCanCreateClient())
        .isInstanceOf(PlanLimitException.class)
        .extracting(exception -> ((PlanLimitException) exception).getCode())
        .isEqualTo(PlanLimitException.CLIENTS);
  }

  @Test
  void proUserIgnoresClientLimit() {
    User user = proUser();
    when(currentUserProvider.getCurrentUser()).thenReturn(user);

    planLimitService.assertCanCreateClient();

    verifyNoInteractions(clientRepository);
  }

  @Test
  void freeUserCannotCreateSixthProposalInCycle() {
    User user = freeUser();
    when(currentUserProvider.getCurrentUser()).thenReturn(user);
    when(proposalRepository.countByUserIdAndCreatedAtGreaterThanEqual(
            org.mockito.ArgumentMatchers.eq(user.getId()),
            org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
        .thenReturn(5L);

    assertThatThrownBy(() -> planLimitService.assertCanCreateProposal())
        .isInstanceOf(PlanLimitException.class)
        .extracting(exception -> ((PlanLimitException) exception).getCode())
        .isEqualTo(PlanLimitException.PROPOSALS);
  }

  @Test
  void usageMarksLimitsReachedOnFreePlan() {
    User user = freeUser();
    when(clientRepository.countByUserId(user.getId())).thenReturn(10L);
    when(proposalRepository.countByUserIdAndCreatedAtGreaterThanEqual(
            org.mockito.ArgumentMatchers.eq(user.getId()),
            org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
        .thenReturn(5L);

    PlanUsageResponse usage = planLimitService.usageOf(user, LocalDateTime.of(2026, 8, 26, 12, 0));

    assertThat(usage.clientsLimitReached()).isTrue();
    assertThat(usage.proposalsLimitReached()).isTrue();
    assertThat(usage.clientsLimit()).isEqualTo(10);
    assertThat(usage.proposalsLimit()).isEqualTo(5);
  }

  @Test
  void usageHasNoCapsOnProPlan() {
    User user = proUser();
    when(clientRepository.countByUserId(user.getId())).thenReturn(42L);
    when(proposalRepository.countByUserIdAndCreatedAtGreaterThanEqual(
            org.mockito.ArgumentMatchers.eq(user.getId()),
            org.mockito.ArgumentMatchers.any(LocalDateTime.class)))
        .thenReturn(20L);

    PlanUsageResponse usage = planLimitService.usageOf(user, LocalDateTime.of(2026, 8, 26, 12, 0));

    assertThat(usage.clientsLimit()).isNull();
    assertThat(usage.proposalsLimit()).isNull();
    assertThat(usage.clientsLimitReached()).isFalse();
    assertThat(usage.proposalsLimitReached()).isFalse();
  }

  private static User freeUser() {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setPlan(UserPlan.FREE);
    user.setCreatedAt(LocalDateTime.of(2026, 3, 15, 10, 0));
    return user;
  }

  private static User proUser() {
    User user = freeUser();
    user.setPlan(UserPlan.PRO);
    return user;
  }
}
