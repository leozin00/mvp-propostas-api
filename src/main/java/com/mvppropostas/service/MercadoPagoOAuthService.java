package com.mvppropostas.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.dto.mercadopago.MpOAuthTokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoOAuthService {

  private static final Duration STATE_TTL = Duration.ofMinutes(10);

  private final MercadoPagoProperties properties;
  private final ConcurrentHashMap<String, Instant> pendingStates = new ConcurrentHashMap<>();

  public String buildAuthorizationUrl() {
    if (!properties.hasOAuthConfig()) {
      throw new BusinessException(
          "OAuth não configurado. Defina MERCADOPAGO_APPLICATION_ID, MERCADOPAGO_CLIENT_SECRET "
              + "e cadastre a Redirect URL no painel do Mercado Pago: "
              + properties.effectiveOauthRedirectUri());
    }

    String state = UUID.randomUUID().toString();
    pendingStates.put(state, Instant.now());
    purgeExpiredStates();

    String redirectUri = encode(properties.effectiveOauthRedirectUri());
    return "https://auth.mercadopago.com/authorization?response_type=code&client_id="
        + encode(properties.effectiveApplicationId())
        + "&redirect_uri="
        + redirectUri
        + "&state="
        + encode(state);
  }

  public MpOAuthTokenResponse exchangeAuthorizationCode(String code, String state) {
    validateState(state);
    if (!properties.hasOAuthConfig()) {
      throw new BusinessException("OAuth não configurado para trocar o código de autorização.");
    }

    Map<String, Object> payload =
        Map.of(
            "client_id",
            properties.effectiveApplicationId(),
            "client_secret",
            properties.clientSecret(),
            "grant_type",
            "authorization_code",
            "code",
            code,
            "redirect_uri",
            properties.effectiveOauthRedirectUri(),
            "test_token",
            true);

    try {
      return RestClient.builder()
          .baseUrl(properties.apiBaseUrlOrDefault())
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build()
          .post()
          .uri("/oauth/token")
          .body(payload)
          .retrieve()
          .body(MpOAuthTokenResponse.class);
    } catch (RestClientResponseException exception) {
      log.warn("Mercado Pago rejeitou troca OAuth: {}", exception.getResponseBodyAsString());
      throw new BusinessException(
          "Não foi possível obter o token sandbox do vendedor de teste. "
              + "Confira Client Secret, Redirect URL e se entrou com a conta vendedor de teste.");
    }
  }

  private void validateState(String state) {
    if (state == null || state.isBlank()) {
      throw new BusinessException("Parâmetro state ausente no retorno OAuth.");
    }
    Instant createdAt = pendingStates.remove(state.trim());
    if (createdAt == null || createdAt.isBefore(Instant.now().minus(STATE_TTL))) {
      throw new BusinessException("Sessão OAuth expirada. Inicie novamente em /oauth/start.");
    }
  }

  private void purgeExpiredStates() {
    Instant cutoff = Instant.now().minus(STATE_TTL);
    pendingStates.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
