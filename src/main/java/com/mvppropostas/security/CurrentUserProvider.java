package com.mvppropostas.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.mvppropostas.common.exception.UnauthorizedException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.service.UserProvisioningService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

  private final UserProvisioningService userProvisioningService;

  public UUID getCurrentUserId() {
    return getCurrentUser().getId();
  }

  public User getCurrentUser() {
    return userProvisioningService.resolve(requireJwt());
  }

  private static Jwt requireJwt() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
      return jwtAuthentication.getToken();
    }
    throw new UnauthorizedException("Autenticação obrigatória.");
  }
}
