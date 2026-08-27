package com.mvppropostas.config;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mercadopago")
public record MercadoPagoProperties(
    String accessToken,
    String sandboxAccessToken,
    String applicationId,
    String clientSecret,
    String oauthRedirectUri,
    String webhookSecret,
    String proPlanId,
    String backUrl,
    String testPayerEmail,
    String testPayerUserId,
    String apiBaseUrl) {

  public static final BigDecimal PRO_AMOUNT = new BigDecimal("24.90");
  public static final String PRO_CURRENCY = "BRL";
  public static final String PRO_REASON = "Plano Pro";
  public static final String DEFAULT_BACK_URL = "https://www.mercadopago.com.br";
  public static final String DEFAULT_OAUTH_REDIRECT_URI =
      "http://localhost:8080/api/v1/dev/mercadopago/oauth/callback";
  private static final String TEST_USER_PREFIX = "TESTUSER";

  public boolean isConfigured() {
    return effectiveAccessToken() != null && !effectiveAccessToken().isBlank();
  }

  public boolean hasWebhookSecret() {
    return webhookSecret != null && !webhookSecret.isBlank();
  }

  public boolean hasProPlanId() {
    return proPlanId != null && !proPlanId.isBlank();
  }

  public boolean hasSandboxAccessToken() {
    return sandboxAccessToken != null && !sandboxAccessToken.isBlank();
  }

  public boolean isSandbox() {
    if (hasSandboxAccessToken()) {
      return true;
    }
    return accessToken != null && accessToken.startsWith("TEST-");
  }

  public boolean hasTestPayerEmail() {
    return testPayerEmail != null && !testPayerEmail.isBlank();
  }

  public boolean hasTestPayerUserId() {
    return testPayerUserId != null && !testPayerUserId.isBlank();
  }

  public boolean hasSandboxPayerConfig() {
    return hasTestPayerEmail() || hasTestPayerUserId();
  }

  public boolean hasOAuthConfig() {
    return effectiveApplicationId() != null
        && !effectiveApplicationId().isBlank()
        && clientSecret != null
        && !clientSecret.isBlank()
        && effectiveOauthRedirectUri() != null
        && !effectiveOauthRedirectUri().isBlank();
  }

  public String effectiveAccessToken() {
    if (hasSandboxAccessToken()) {
      return sandboxAccessToken.trim();
    }
    if (accessToken == null || accessToken.isBlank()) {
      return null;
    }
    return accessToken.trim();
  }

  public String effectiveApplicationId() {
    if (applicationId != null && !applicationId.isBlank()) {
      return applicationId.trim();
    }
    return null;
  }

  public String effectiveOauthRedirectUri() {
    if (oauthRedirectUri != null && !oauthRedirectUri.isBlank()) {
      return oauthRedirectUri.trim();
    }
    return DEFAULT_OAUTH_REDIRECT_URI;
  }

  /**
   * Test accounts expose the nickname (TESTUSER1234), never the e-mail. Mercado Pago derives the
   * address from the nickname digits, so {@code TESTUSER1234} maps to
   * {@code test_user_1234@testuser.com}. Using the numeric User ID instead is rejected with 400.
   */
  public static String sandboxPayerEmailForNickname(String nickname) {
    String normalized = nickname.trim();
    String suffix =
        isTestUserNickname(normalized) ? normalized.substring(TEST_USER_PREFIX.length()) : normalized;
    return "test_user_" + suffix + "@testuser.com";
  }

  public static boolean isTestUserNickname(String nickname) {
    return nickname != null && nickname.trim().toUpperCase(Locale.ROOT).startsWith(TEST_USER_PREFIX);
  }

  public String apiBaseUrlOrDefault() {
    if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
      return "https://api.mercadopago.com";
    }
    return apiBaseUrl;
  }

  /**
   * Mercado Pago rejects localhost, HTTP and query strings on {@code back_url}.
   * Local/dev values fall back to a public HTTPS URL; after paying, the user
   * returns to the app and syncs the subscription from the profile.
   */
  public String effectiveBackUrl() {
    return isUsableBackUrl(backUrl) ? backUrl.trim() : DEFAULT_BACK_URL;
  }

  public static boolean isUsableBackUrl(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    try {
      URI uri = URI.create(url.trim());
      String host = uri.getHost();
      return "https".equalsIgnoreCase(uri.getScheme())
          && host != null
          && !host.equalsIgnoreCase("localhost")
          && !host.equals("127.0.0.1")
          && uri.getQuery() == null
          && uri.getFragment() == null;
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }
}
