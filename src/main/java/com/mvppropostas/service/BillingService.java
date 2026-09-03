package com.mvppropostas.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.client.MercadoPagoClient;
import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.domain.entity.Subscription;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.mercadopago.MpAuthorizedPaymentResponse;
import com.mvppropostas.dto.mercadopago.MpPreapprovalResponse;
import com.mvppropostas.dto.mercadopago.MpWebhookPayload;
import com.mvppropostas.dto.response.BillingCheckoutResponse;
import com.mvppropostas.dto.response.BillingStatusResponse;
import com.mvppropostas.repository.SubscriptionRepository;
import com.mvppropostas.repository.UserRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

  private static final String PENDING = "pending";
  private static final String AUTHORIZED = "authorized";

  private final CurrentUserProvider currentUserProvider;
  private final MercadoPagoProperties properties;
  private final MercadoPagoClient mercadoPagoClient;
  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final PlanLimitService planLimitService;

  @Transactional(readOnly = true)
  public BillingStatusResponse getStatus() {
    User user = currentUserProvider.getCurrentUser();
    String subscriptionStatus =
        subscriptionRepository
            .findTopByUserIdOrderByCreatedAtDesc(user.getId())
            .map(Subscription::getStatus)
            .orElse(null);
    return new BillingStatusResponse(
        user.getPlan(),
        subscriptionStatus,
        properties.isConfigured(),
        properties.isSandbox(),
        isSandboxTestMode(),
        MercadoPagoProperties.PRO_AMOUNT,
        MercadoPagoProperties.PRO_CURRENCY,
        planLimitService.currentUsage());
  }

  @Transactional
  public BillingCheckoutResponse createCheckout() {
    User user = currentUserProvider.getCurrentUser();
    if (user.getPlan() == UserPlan.PRO) {
      throw new BusinessException("Sua conta já está no plano Pro.");
    }
    if (!properties.isConfigured()) {
      throw new BusinessException(
          "Checkout Pro ainda não está configurado. Defina MERCADOPAGO_ACCESS_TOKEN.");
    }
    ensureSandboxReady();
    String payerEmail = resolvePayerEmail(user.getEmail());

    Optional<Subscription> latest =
        subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
    if (latest.isPresent() && isPending(latest.get().getStatus())) {
      Subscription pending = latest.get();
      if (matchesPayerEmail(pending.getPayerEmail(), payerEmail)) {
        MpPreapprovalResponse current =
            mercadoPagoClient.getPreapproval(pending.getMpPreapprovalId());
        if (current != null) {
          applyPreapproval(current);
          if (isPending(current.status()) && current.checkoutUrl() != null) {
            return new BillingCheckoutResponse(current.checkoutUrl());
          }
          if (isAuthorized(current.status())) {
            throw new BusinessException("Sua conta já está no plano Pro.");
          }
        }
      } else {
        cancelPendingSubscription(pending);
      }
    }

    MpPreapprovalResponse created =
        mercadoPagoClient.createPreapproval(
            payerEmail, user.getId().toString(), properties.effectiveBackUrl());
    if (created == null || created.id() == null || created.checkoutUrl() == null) {
      throw new BusinessException("O Mercado Pago não retornou o link de checkout.");
    }
    upsertSubscription(user.getId(), created, payerEmail);
    return new BillingCheckoutResponse(created.checkoutUrl());
  }

  @Transactional
  public BillingStatusResponse syncCurrentUser() {
    User user = currentUserProvider.getCurrentUser();
    subscriptionRepository
        .findTopByUserIdOrderByCreatedAtDesc(user.getId())
        .ifPresent(subscription -> applyPreapproval(
            mercadoPagoClient.getPreapproval(subscription.getMpPreapprovalId())));
    return getStatus();
  }

  @Transactional
  public void handleWebhook(MpWebhookPayload payload, String queryDataId) {
    String type = payload == null ? null : payload.type();
    String dataId = firstNonBlank(queryDataId, payload == null || payload.data() == null ? null : payload.data().id());
    if (dataId == null) {
      log.warn("Webhook Mercado Pago sem data.id (type={})", type);
      return;
    }

    String preapprovalId = resolvePreapprovalId(type, dataId);
    if (preapprovalId == null) {
      return;
    }
    applyPreapproval(mercadoPagoClient.getPreapproval(preapprovalId));
  }

  void applyPreapproval(MpPreapprovalResponse preapproval) {
    if (preapproval == null || preapproval.id() == null) {
      return;
    }

    UUID userId = resolveUserId(preapproval);
    if (userId == null) {
      log.warn("Assinatura {} sem usuário associado", preapproval.id());
      return;
    }

    Subscription subscription = upsertSubscription(userId, preapproval, preapproval.payerEmail());
    userRepository
        .findById(userId)
        .ifPresent(
            user -> {
              UserPlan nextPlan = isAuthorized(subscription.getStatus()) ? UserPlan.PRO : UserPlan.FREE;
              if (user.getPlan() != nextPlan) {
                user.setPlan(nextPlan);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
              }
            });
  }

  private Subscription upsertSubscription(
      UUID userId, MpPreapprovalResponse preapproval, String payerEmail) {
    LocalDateTime now = LocalDateTime.now();
    Subscription subscription =
        subscriptionRepository
            .findByMpPreapprovalId(preapproval.id())
            .orElseGet(
                () -> {
                  Subscription created = new Subscription();
                  created.setId(UUID.randomUUID());
                  created.setUserId(userId);
                  created.setMpPreapprovalId(preapproval.id());
                  created.setCreatedAt(now);
                  return created;
                });
    subscription.setMpPreapprovalPlanId(preapproval.preapprovalPlanId());
    subscription.setStatus(normalizeStatus(preapproval.status()));
    if (preapproval.checkoutUrl() != null) {
      subscription.setCheckoutUrl(preapproval.checkoutUrl());
    }
    if (payerEmail != null && !payerEmail.isBlank()) {
      subscription.setPayerEmail(payerEmail);
    }
    subscription.setUpdatedAt(now);
    return subscriptionRepository.save(subscription);
  }

  private UUID resolveUserId(MpPreapprovalResponse preapproval) {
    return subscriptionRepository
        .findByMpPreapprovalId(preapproval.id())
        .map(Subscription::getUserId)
        .orElseGet(() -> parseUuid(preapproval.externalReference()));
  }

  private void cancelPendingSubscription(Subscription subscription) {
    subscription.setStatus("cancelled");
    subscription.setUpdatedAt(LocalDateTime.now());
    subscriptionRepository.save(subscription);
  }

  private static boolean matchesPayerEmail(String storedPayerEmail, String payerEmail) {
    return storedPayerEmail != null && storedPayerEmail.equalsIgnoreCase(payerEmail);
  }

  private void ensureSandboxReady() {
    if (!properties.isSandbox()) {
      return;
    }
    if (!mercadoPagoClient.isTestCollector()) {
      throw new BusinessException(
          "Para homologação com contas de teste, gere MERCADOPAGO_SANDBOX_ACCESS_TOKEN "
              + "via OAuth com a conta vendedor de teste: "
              + "http://localhost:8080/api/v1/dev/mercadopago/oauth/start");
    }
    if (!properties.hasSandboxPayerConfig()) {
      throw new BusinessException(
          "Defina MERCADOPAGO_TEST_PAYER_USER_ID com o User ID do comprador de teste "
              + "(painel Developers → GestaoPropostas → Contas de teste).");
    }
  }

  private boolean isSandboxTestMode() {
    return properties.isSandbox() && mercadoPagoClient.isTestCollector();
  }

  private String resolvePayerEmail(String userEmail) {
    if (!properties.isSandbox()) {
      return userEmail;
    }
    if (!mercadoPagoClient.isTestCollector()) {
      return userEmail;
    }
    if (properties.hasTestPayerEmail()) {
      return properties.testPayerEmail().trim();
    }
    if (properties.hasTestPayerUserId()) {
      return mercadoPagoClient.resolveTestPayerEmail(properties.testPayerUserId().trim());
    }
    throw new BusinessException(
        "Defina MERCADOPAGO_TEST_PAYER_USER_ID com o User ID do comprador de teste.");
  }

  private String resolvePreapprovalId(String type, String dataId) {
    if (type == null || type.isBlank() || "subscription_preapproval".equals(type)) {
      return dataId;
    }
    if ("subscription_authorized_payment".equals(type)) {
      MpAuthorizedPaymentResponse payment = mercadoPagoClient.getAuthorizedPayment(dataId);
      return payment == null ? null : payment.preapprovalId();
    }
    log.info("Webhook Mercado Pago ignorado (type={})", type);
    return null;
  }

  private static boolean isPending(String status) {
    return PENDING.equals(normalizeStatus(status));
  }

  private static boolean isAuthorized(String status) {
    return AUTHORIZED.equals(normalizeStatus(status));
  }

  private static String normalizeStatus(String status) {
    if (status == null || status.isBlank()) {
      return PENDING;
    }
    return status.trim().toLowerCase(Locale.ROOT);
  }

  private static UUID parseUuid(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value.trim());
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  private static String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }
}
