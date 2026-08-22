package com.mvppropostas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.domain.entity.User;
import com.mvppropostas.dto.response.UserProfileResponse;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

  private final CurrentUserProvider currentUserProvider;

  public UserProfileResponse getProfile() {
    User user = currentUserProvider.getCurrentUser();
    return new UserProfileResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPlan(),
        user.getCompanyName(),
        user.getContactEmail(),
        user.getContactPhone());
  }
}
