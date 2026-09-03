package com.mvppropostas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.mvppropostas.client.MercadoPagoClient;
import com.mvppropostas.config.AdminProperties;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.response.AdminAccountResponse;
import com.mvppropostas.dto.response.AdminAccountsPageResponse;
import com.mvppropostas.dto.response.AdminMetricsResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.repository.SubscriptionRepository;
import com.mvppropostas.repository.UserRepository;
import com.mvppropostas.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private UserRepository userRepository;
  @Mock private ClientRepository clientRepository;
  @Mock private ProposalRepository proposalRepository;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private MercadoPagoProperties mercadoPagoProperties;
  @Mock private MercadoPagoClient mercadoPagoClient;

  private AdminAccessService adminAccessService;
  private AdminService adminService;

  @BeforeEach
  void setUp() {
    adminAccessService =
        new AdminAccessService(new AdminProperties("admin@test.com"), currentUserProvider);
    adminService =
        new AdminService(
            adminAccessService,
            userRepository,
            clientRepository,
            proposalRepository,
            subscriptionRepository,
            mercadoPagoProperties,
            mercadoPagoClient);
  }

  @Test
  void rejectsNonAdmin() {
    User user = user("comum@test.com", UserPlan.FREE);
    when(currentUserProvider.getCurrentUser()).thenReturn(user);

    assertThatThrownBy(() -> adminService.getMetrics(30))
        .isInstanceOf(AccessDeniedException.class)
        .hasMessageContaining("administradores");
    verify(userRepository, never()).count();
  }

  @Test
  void aggregatesMetricsForAdmin() {
    when(currentUserProvider.getCurrentUser()).thenReturn(user("admin@test.com", UserPlan.PRO));
    when(userRepository.count()).thenReturn(10L);
    when(userRepository.countByPlan(UserPlan.FREE)).thenReturn(7L);
    when(userRepository.countByPlan(UserPlan.PRO)).thenReturn(3L);
    when(userRepository.countByLastLoginGreaterThanEqual(any())).thenReturn(4L);
    when(userRepository.countByCreatedAtGreaterThanEqual(any())).thenReturn(2L);
    when(subscriptionRepository.countByCreatedAtGreaterThanEqual(any())).thenReturn(1L);
    when(subscriptionRepository.countByUpdatedAtGreaterThanEqualAndStatusNotIgnoreCase(
            any(), eq("authorized")))
        .thenReturn(1L);
    when(proposalRepository.countByDeletedAtIsNull()).thenReturn(40L);
    when(clientRepository.count()).thenReturn(25L);

    AdminMetricsResponse metrics = adminService.getMetrics(7);

    assertThat(metrics.periodDays()).isEqualTo(7);
    assertThat(metrics.totalUsers()).isEqualTo(10);
    assertThat(metrics.activeUsers()).isEqualTo(4);
    assertThat(metrics.freeUsers()).isEqualTo(7);
    assertThat(metrics.proUsers()).isEqualTo(3);
    assertThat(metrics.freeUsers() + metrics.proUsers()).isEqualTo(metrics.totalUsers());
    assertThat(metrics.newUsers()).isEqualTo(2);
    assertThat(metrics.subscriptions()).isEqualTo(1);
    assertThat(metrics.cancellations()).isEqualTo(1);
    assertThat(metrics.mrr()).isEqualByComparingTo(new BigDecimal("74.70"));
    assertThat(metrics.currency()).isEqualTo(MercadoPagoProperties.PRO_CURRENCY);
    assertThat(metrics.totalProposals()).isEqualTo(40);
    assertThat(metrics.totalClients()).isEqualTo(25);
  }

  @Test
  void defaultsInvalidPeriodToThirtyDays() {
    when(currentUserProvider.getCurrentUser()).thenReturn(user("admin@test.com", UserPlan.PRO));
    when(userRepository.count()).thenReturn(0L);
    when(userRepository.countByPlan(UserPlan.FREE)).thenReturn(0L);
    when(userRepository.countByPlan(UserPlan.PRO)).thenReturn(0L);
    when(userRepository.countByLastLoginGreaterThanEqual(any())).thenReturn(0L);
    when(userRepository.countByCreatedAtGreaterThanEqual(any())).thenReturn(0L);
    when(subscriptionRepository.countByCreatedAtGreaterThanEqual(any())).thenReturn(0L);
    when(subscriptionRepository.countByUpdatedAtGreaterThanEqualAndStatusNotIgnoreCase(
            any(), eq("authorized")))
        .thenReturn(0L);
    when(proposalRepository.countByDeletedAtIsNull()).thenReturn(0L);
    when(clientRepository.count()).thenReturn(0L);

    AdminMetricsResponse metrics = adminService.getMetrics(15);

    assertThat(metrics.periodDays()).isEqualTo(30);
    ArgumentCaptor<LocalDateTime> since = ArgumentCaptor.forClass(LocalDateTime.class);
    verify(userRepository).countByCreatedAtGreaterThanEqual(since.capture());
    assertThat(since.getValue()).isBefore(LocalDateTime.now().minusDays(29));
  }

  @Test
  void allTimePeriodUsesUnboundedCounts() {
    when(currentUserProvider.getCurrentUser()).thenReturn(user("admin@test.com", UserPlan.PRO));
    when(userRepository.count()).thenReturn(10L);
    when(userRepository.countByPlan(UserPlan.FREE)).thenReturn(7L);
    when(userRepository.countByPlan(UserPlan.PRO)).thenReturn(3L);
    when(userRepository.countByLastLoginIsNotNull()).thenReturn(8L);
    when(subscriptionRepository.count()).thenReturn(5L);
    when(subscriptionRepository.countByStatusNotIgnoreCase("authorized")).thenReturn(2L);
    when(proposalRepository.countByDeletedAtIsNull()).thenReturn(40L);
    when(clientRepository.count()).thenReturn(25L);

    AdminMetricsResponse metrics = adminService.getMetrics(0);

    assertThat(metrics.periodDays()).isEqualTo(0);
    assertThat(metrics.newUsers()).isEqualTo(10);
    assertThat(metrics.activeUsers()).isEqualTo(8);
    assertThat(metrics.subscriptions()).isEqualTo(5);
    assertThat(metrics.cancellations()).isEqualTo(2);
    verify(userRepository, never()).countByCreatedAtGreaterThanEqual(any());
  }

  @Test
  void mapsAccountsWithoutSensitiveFields() {
    when(currentUserProvider.getCurrentUser()).thenReturn(user("admin@test.com", UserPlan.PRO));
    User listed = user("seller@test.com", UserPlan.FREE);
    listed.setPasswordHash("secret-hash");
    listed.setFirebaseUid("firebase-uid");
    when(userRepository.searchAccounts(eq(""), isNull(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(listed), PageRequest.of(0, 20), 1));

    AdminAccountsPageResponse page = adminService.listAccounts("  ", null, 0, 20, "createdAt", "desc");
    AdminAccountResponse account = page.items().get(0);

    assertThat(account.email()).isEqualTo("seller@test.com");
    assertThat(account.plan()).isEqualTo(UserPlan.FREE);
    assertThat(account.name()).isEqualTo("Seller");
    assertThat(AdminAccountResponse.class.getRecordComponents())
        .extracting(component -> component.getName())
        .containsExactly("id", "name", "email", "plan", "active", "createdAt");
  }

  @Test
  void emptyAllowlistDeniesEveryone() {
    AdminAccessService empty =
        new AdminAccessService(new AdminProperties(""), currentUserProvider);
    when(currentUserProvider.getCurrentUser()).thenReturn(user("admin@test.com", UserPlan.PRO));

    assertThat(empty.isAdmin("admin@test.com")).isFalse();
    assertThatThrownBy(empty::requireAdmin).isInstanceOf(AccessDeniedException.class);
  }

  @Test
  void changePlanUpgradesAccountToPro() {
    User admin = user("admin@test.com", UserPlan.PRO);
    User target = user("seller@test.com", UserPlan.FREE);
    when(currentUserProvider.getCurrentUser()).thenReturn(admin);
    when(userRepository.findById(target.getId())).thenReturn(java.util.Optional.of(target));
    when(userRepository.save(target)).thenReturn(target);

    AdminAccountResponse updated = adminService.changePlan(target.getId(), UserPlan.PRO);

    assertThat(updated.plan()).isEqualTo(UserPlan.PRO);
    assertThat(target.getPlan()).isEqualTo(UserPlan.PRO);
  }

  @Test
  void cancelAccountDeactivatesAndBlocksSelfCancel() {
    User admin = user("admin@test.com", UserPlan.PRO);
    User target = user("seller@test.com", UserPlan.PRO);
    when(currentUserProvider.getCurrentUser()).thenReturn(admin);
    when(userRepository.findById(target.getId())).thenReturn(java.util.Optional.of(target));
    when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
    when(subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(target.getId()))
        .thenReturn(java.util.Optional.empty());
    when(userRepository.save(target)).thenReturn(target);

    AdminAccountResponse cancelled = adminService.cancelAccount(target.getId());
    assertThat(cancelled.active()).isFalse();
    assertThat(cancelled.plan()).isEqualTo(UserPlan.FREE);

    assertThatThrownBy(() -> adminService.cancelAccount(admin.getId()))
        .isInstanceOf(com.mvppropostas.common.exception.BusinessException.class)
        .hasMessageContaining("própria conta");
  }

  private static User user(String email, UserPlan plan) {
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setName(email.startsWith("admin") ? "Admin" : "Seller");
    user.setEmail(email);
    user.setPlan(plan);
    user.setActive(true);
    user.setCreatedAt(LocalDateTime.now().minusDays(10));
    return user;
  }
}
