package com.mvppropostas.security;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

  private final UserRepository userRepository;

  @Value("${app.demo.user-id}")
  private UUID demoUserId;

  public UUID getCurrentUserId() {
    return demoUserId;
  }

  public User getCurrentUser() {
    return userRepository
        .findById(demoUserId)
        .orElseThrow(() -> new BusinessException("Usuário demo não encontrado."));
  }
}
