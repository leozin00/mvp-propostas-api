package com.mvppropostas.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@Profile("!test")
public class FirebaseJwtConfig {

  @Bean
  JwtDecoder jwtDecoder(@Value("${app.firebase.project-id}") String projectId) {
    String issuer = "https://securetoken.google.com/" + projectId;
    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withJwkSetUri(
                "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com")
            .build();

    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
    decoder.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator(projectId)));
    return decoder;
  }

  private static OAuth2TokenValidator<Jwt> audienceValidator(String projectId) {
    return jwt -> {
      if (containsAudience(jwt.getClaim("aud"), projectId)
          || (jwt.getAudience() != null && jwt.getAudience().contains(projectId))) {
        return OAuth2TokenValidatorResult.success();
      }
      return OAuth2TokenValidatorResult.failure(
          new OAuth2Error("invalid_token", "Audience do token Firebase inválida.", null));
    };
  }

  private static boolean containsAudience(Object audience, String projectId) {
    if (audience instanceof String value) {
      return projectId.equals(value);
    }
    if (audience instanceof Collection<?> values) {
      return values.contains(projectId);
    }
    return false;
  }
}

