package com.mvppropostas.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mvppropostas.common.exception.EmailNotVerifiedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class EmailVerifiedFilter extends OncePerRequestFilter {

  private final boolean required;

  public EmailVerifiedFilter(
      @Value("${app.auth.require-email-verified:true}") boolean required) {
    this.required = required;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if (!required) {
      return true;
    }
    String path = request.getRequestURI();
    return path.startsWith("/api/v1/health")
        || path.startsWith("/actuator/")
        || path.startsWith("/api/v1/public/")
        || path.startsWith("/api/v1/webhooks/")
        || path.startsWith("/api/v1/dev/")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/swagger-ui");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthentication && jwtAuthentication.isAuthenticated()) {
      Jwt jwt = jwtAuthentication.getToken();
      if (!isEmailVerified(jwt)) {
        writeForbidden(response);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private static boolean isEmailVerified(Jwt jwt) {
    Object claim = jwt.getClaim("email_verified");
    if (claim instanceof Boolean verified) {
      return verified;
    }
    return "true".equalsIgnoreCase(String.valueOf(claim));
  }

  private static void writeForbidden(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    String body =
        """
        {"timestamp":"%s","status":403,"error":"Forbidden","code":"%s","message":"%s"}
        """
            .formatted(
                Instant.now(),
                EmailNotVerifiedException.CODE,
                "Confirme seu e-mail para continuar.");
    response.getWriter().write(body);
  }
}
