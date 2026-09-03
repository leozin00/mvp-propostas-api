package com.mvppropostas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvppropostas.client.MercadoPagoClient;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.mercadopago.MpPreapprovalResponse;
import com.mvppropostas.repository.SubscriptionRepository;
import com.mvppropostas.repository.UserRepository;
import com.mvppropostas.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

  @Mock private CurrentUserProvider currentUserProvider;
  @Mock private MercadoPagoProperties properties;
  @Mock private MercadoPagoClient mercadoPagoClient;
  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private UserRepository userRepository;
  @Mock private PlanLimitService planLimitService;

  @InjectMocks private BillingService billingService;

  @Test
  void authorizedPreapprovalUpgradesUserToPro() {
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setPlan(UserPlan.FREE);

    MpPreapprovalResponse preapproval =
        new MpPreapprovalResponse(
            "preapproval-1",
            "authorized",
            "https://pay.mercadopago.com/checkout",
            null,
            userId.toString(),
            null,
            "payer@test.com");

    when(subscriptionRepository.findByMpPreapprovalId("preapproval-1")).thenReturn(Optional.empty());
    when(subscriptionRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    billingService.applyPreapproval(preapproval);

    assertThat(user.getPlan()).isEqualTo(UserPlan.PRO);
  }

  @Test
  void cancelledPreapprovalDowngradesUserToFree() {
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);
    user.setPlan(UserPlan.PRO);

    MpPreapprovalResponse preapproval =
        new MpPreapprovalResponse(
            "preapproval-2",
            "cancelled",
            null,
            null,
            userId.toString(),
            null,
            "payer@test.com");

    when(subscriptionRepository.findByMpPreapprovalId("preapproval-2")).thenReturn(Optional.empty());
    when(subscriptionRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    billingService.applyPreapproval(preapproval);

    assertThat(user.getPlan()).isEqualTo(UserPlan.FREE);
  }
}

class MercadoPagoSignatureVerifierTest {

  private final MercadoPagoSignatureVerifier verifier = new MercadoPagoSignatureVerifier();

  @Test
  void acceptsRequestWhenSecretIsNotConfigured() {
    assertThat(verifier.isValid(" ", "ts=1,v1=abc", "req", "123")).isTrue();
  }

  @Test
  void rejectsMissingSignatureWhenSecretIsConfigured() {
    assertThat(verifier.isValid("super-secret", null, "req", "123")).isFalse();
  }

  @Test
  void acceptsMatchingHmacSignature() throws Exception {
    String secret = "whsec";
    String dataId = "2C938084ABC";
    String requestId = "req-1";
    String timestamp = "1700000000";
    String manifest = MercadoPagoSignatureVerifier.buildManifest(dataId, requestId, timestamp);
    String digest = hmac(secret, manifest);

    assertThat(verifier.isValid(secret, "ts=" + timestamp + ",v1=" + digest, requestId, dataId))
        .isTrue();
  }

  private static String hmac(String secret, String message) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
  }
}

class MercadoPagoPropertiesTest {

  @Test
  void detectsSandboxToken() {
    MercadoPagoProperties sandbox =
        new MercadoPagoProperties(
            "TEST-abc", null, null, null, null, null, null, null, "buyer@testuser.com", "123", null);
    MercadoPagoProperties production =
        new MercadoPagoProperties(
            "APP_USR-abc", null, null, null, null, null, null, null, null, null, null);

    org.assertj.core.api.Assertions.assertThat(sandbox.isSandbox()).isTrue();
    org.assertj.core.api.Assertions.assertThat(sandbox.hasTestPayerEmail()).isTrue();
    org.assertj.core.api.Assertions.assertThat(sandbox.hasSandboxPayerConfig()).isTrue();
    org.assertj.core.api.Assertions.assertThat(production.isSandbox()).isFalse();
  }

  @Test
  void buildsSandboxPayerEmailFromNickname() {
    org.assertj.core.api.Assertions.assertThat(
            MercadoPagoProperties.sandboxPayerEmailForNickname("TESTUSER3931418361817184503"))
        .isEqualTo("test_user_3931418361817184503@testuser.com");
    org.assertj.core.api.Assertions.assertThat(
            MercadoPagoProperties.isTestUserNickname("TESTUSER5590168606942123234"))
        .isTrue();
    org.assertj.core.api.Assertions.assertThat(MercadoPagoProperties.isTestUserNickname("VALE7162756"))
        .isFalse();
  }

  @Test
  void prefersSandboxAccessToken() {
    MercadoPagoProperties properties =
        new MercadoPagoProperties(
            "TEST-panel-token",
            "TEST-seller-oauth-token",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    org.assertj.core.api.Assertions.assertThat(properties.effectiveAccessToken())
        .isEqualTo("TEST-seller-oauth-token");
    org.assertj.core.api.Assertions.assertThat(properties.isSandbox()).isTrue();
  }

  @Test
  void rejectsLocalhostHttpAndQueryString() {
    org.assertj.core.api.Assertions.assertThat(
            MercadoPagoProperties.isUsableBackUrl("http://localhost:4200/profile?billing=return"))
        .isFalse();
    org.assertj.core.api.Assertions.assertThat(
            MercadoPagoProperties.isUsableBackUrl("https://www.mercadopago.com.br"))
        .isTrue();
  }
}

class MpPreapprovalResponseTest {

  @Test
  void prefersInitPointForHomologationAndProduction() {
    MpPreapprovalResponse response =
        new MpPreapprovalResponse(
            "id",
            "pending",
            "https://www.mercadopago.com.br/subscriptions/checkout",
            "https://sandbox.mercadopago.com.br/subscriptions/checkout",
            "ref",
            null,
            "a@b.com");

    org.assertj.core.api.Assertions.assertThat(response.checkoutUrl())
        .isEqualTo("https://www.mercadopago.com.br/subscriptions/checkout");
  }
}
