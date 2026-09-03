package com.mvppropostas.service;

import java.util.Locale;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.mvppropostas.config.AdminProperties;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAccessService {

  private final AdminProperties adminProperties;
  private final CurrentUserProvider currentUserProvider;

  public User requireAdmin() {
    User user = currentUserProvider.getCurrentUser();
    if (!isAdmin(user.getEmail())) {
      throw new AccessDeniedException("Acesso restrito a administradores.");
    }
    return user;
  }

  public boolean isAdmin(String email) {
    if (email == null || email.isBlank()) {
      return false;
    }
    return adminProperties.allowedEmails().contains(email.trim().toLowerCase(Locale.ROOT));
  }
}
