package com.mvppropostas.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.dto.mercadopago.MpAuthorizedPaymentResponse;
import com.mvppropostas.dto.mercadopago.MpCollectorProfile;
import com.mvppropostas.dto.mercadopago.MpPreapprovalResponse;
import com.mvppropostas.dto.mercadopago.MpTestUserResponse;

@Component
@Slf4j
public class MercadoPagoClient {

  private final MercadoPagoProperties properties;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private volatile Boolean testCollector;

  public MercadoPagoClient(MercadoPagoProperties properties) {
    this.properties = properties;
  }

  public MpPreapprovalResponse createPreapproval(
      String payerEmail, String externalReference, String backUrl) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("reason", MercadoPagoProperties.PRO_REASON);
    payload.put("payer_email", payerEmail);
    payload.put("back_url", backUrl);
    payload.put("status", "pending");
    payload.put("external_reference", externalReference);

    if (properties.hasProPlanId() && !properties.isSandbox()) {
      payload.put("preapproval_plan_id", properties.proPlanId());
    } else {
      payload.put(
          "auto_recurring",
          Map.of(
              "frequency",
              1,
              "frequency_type",
              "months",
              "transaction_amount",
              MercadoPagoProperties.PRO_AMOUNT.doubleValue(),
              "currency_id",
              MercadoPagoProperties.PRO_CURRENCY));
    }

    try {
      return restClient()
          .post()
          .uri("/preapproval")
          .body(payload)
          .retrieve()
          .body(MpPreapprovalResponse.class);
    } catch (RestClientResponseException exception) {
      log.warn("Mercado Pago rejeitou criação de assinatura: {}", exception.getResponseBodyAsString());
      throw new BusinessException(
          "Não foi possível iniciar o checkout no Mercado Pago. " + extractMessage(exception));
    } catch (RuntimeException exception) {
      log.error("Falha ao chamar Mercado Pago para criar assinatura", exception);
      throw new BusinessException(
          "Não foi possível iniciar o checkout no Mercado Pago. Tente novamente em instantes.");
    }
  }

  public MpPreapprovalResponse getPreapproval(String id) {
    try {
      return restClient()
          .get()
          .uri("/preapproval/{id}", id)
          .retrieve()
          .body(MpPreapprovalResponse.class);
    } catch (RestClientResponseException exception) {
      throw new BusinessException(
          "Não foi possível consultar a assinatura no Mercado Pago. " + extractMessage(exception));
    }
  }

  public void cancelPreapproval(String id) {
    try {
      restClient()
          .put()
          .uri("/preapproval/{id}", id)
          .body(Map.of("status", "cancelled"))
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException exception) {
      log.warn("Mercado Pago rejeitou cancelamento da assinatura {}: {}", id, exception.getResponseBodyAsString());
      throw new BusinessException(
          "Não foi possível cancelar a assinatura no Mercado Pago. " + extractMessage(exception));
    }
  }

  public MpAuthorizedPaymentResponse getAuthorizedPayment(String id) {
    try {
      return restClient()
          .get()
          .uri("/authorized_payments/{id}", id)
          .retrieve()
          .body(MpAuthorizedPaymentResponse.class);
    } catch (RestClientResponseException exception) {
      throw new BusinessException(
          "Não foi possível consultar o pagamento da assinatura. " + extractMessage(exception));
    }
  }

  public MpCollectorProfile getCollectorProfile() {
    try {
      String body =
          restClient()
              .get()
              .uri("/users/me")
              .retrieve()
              .body(String.class);
      if (body == null || body.isBlank()) {
        return null;
      }
      JsonNode root = objectMapper.readTree(body);
      Long id = root.hasNonNull("id") ? root.get("id").asLong() : null;
      String nickname = root.hasNonNull("nickname") ? root.get("nickname").asText() : null;
      String email = extractEmail(root);
      return new MpCollectorProfile(id, nickname, email);
    } catch (RestClientResponseException exception) {
      log.warn("Mercado Pago não retornou perfil do vendedor: {}", extractMessage(exception));
      return null;
    } catch (Exception exception) {
      log.warn("Não foi possível interpretar perfil do vendedor: {}", exception.getMessage());
      return null;
    }
  }

  public boolean isTestCollector() {
    Boolean cached = testCollector;
    if (cached != null) {
      return cached;
    }
    MpCollectorProfile profile = getCollectorProfile();
    boolean resolved = profile != null && MercadoPagoProperties.isTestUserNickname(profile.nickname());
    testCollector = resolved;
    return resolved;
  }

  public String resolveTestPayerEmail(String userId) {
    String normalizedId = userId.trim();
    try {
      String body = restClient().get().uri("/users/{id}", normalizedId).retrieve().body(String.class);
      if (body != null && !body.isBlank()) {
        JsonNode root = objectMapper.readTree(body);
        String email = extractEmail(root);
        if (email != null && !email.isBlank()) {
          return email.trim();
        }
        if (root.hasNonNull("nickname")) {
          return MercadoPagoProperties.sandboxPayerEmailForNickname(root.get("nickname").asText());
        }
      }
    } catch (RestClientResponseException exception) {
      log.warn(
          "Mercado Pago não retornou o comprador de teste {}: {}",
          normalizedId,
          extractMessage(exception));
    } catch (Exception exception) {
      log.warn(
          "Não foi possível interpretar o comprador de teste {}: {}",
          normalizedId,
          exception.getMessage());
    }
    throw new BusinessException(
        "Não foi possível resolver o e-mail do comprador de teste "
            + normalizedId
            + ". Confira MERCADOPAGO_TEST_PAYER_USER_ID ou defina MERCADOPAGO_TEST_PAYER_EMAIL.");
  }

  public MpTestUserResponse createTestBuyer() {
    Map<String, Object> payload =
        Map.of(
            "site_id",
            "MLB",
            "description",
            "GestaoPropostas checkout buyer",
            "profile",
            "buyer");
    try {
      return restClient()
          .post()
          .uri("/users/test")
          .body(payload)
          .retrieve()
          .body(MpTestUserResponse.class);
    } catch (RestClientResponseException exception) {
      log.warn("Mercado Pago rejeitou criação de comprador de teste: {}", exception.getResponseBodyAsString());
      throw new BusinessException(
          "Não foi possível criar comprador de teste. " + extractMessage(exception));
    }
  }

  private RestClient restClient() {
    if (!properties.isConfigured()) {
      throw new BusinessException(
          "Checkout Pro ainda não está configurado. Defina MERCADOPAGO_ACCESS_TOKEN.");
    }
    return RestClient.builder()
        .baseUrl(properties.apiBaseUrlOrDefault())
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.effectiveAccessToken())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private String extractMessage(RestClientResponseException exception) {
    String body = exception.getResponseBodyAsString();
    if (body == null || body.isBlank()) {
      return "Tente novamente em instantes.";
    }
    try {
      JsonNode root = objectMapper.readTree(body);
      if (root.hasNonNull("message")) {
        return root.get("message").asText();
      }
    } catch (Exception ignored) {
      return "Tente novamente em instantes.";
    }
    return "Tente novamente em instantes.";
  }

  private static String extractEmail(JsonNode root) {
    if (root == null || !root.has("email")) {
      return null;
    }
    JsonNode email = root.get("email");
    if (email.isTextual()) {
      return email.asText();
    }
    if (email.isObject() && email.hasNonNull("address")) {
      return email.get("address").asText();
    }
    return null;
  }
}
