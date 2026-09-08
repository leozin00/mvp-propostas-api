package com.mvppropostas.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.dto.request.ProfileBusinessRequest;
import com.mvppropostas.dto.request.ProfilePersonalRequest;
import com.mvppropostas.dto.response.UserProfileResponse;
import com.mvppropostas.repository.UserRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final CurrentUserProvider currentUserProvider;
  private final UserRepository userRepository;
  private final JdbcTemplate jdbcTemplate;

  @Transactional(readOnly = true)
  public UserProfileResponse getProfile() {
    return toResponse(currentUserProvider.getCurrentUser());
  }

  @Transactional
  public UserProfileResponse updatePersonal(ProfilePersonalRequest request) {
    User user = currentUserProvider.getCurrentUser();
    String email = request.email().trim();

    userRepository
        .findByEmailIgnoreCase(email)
        .filter(existing -> !existing.getId().equals(user.getId()))
        .ifPresent(
            existing -> {
              throw new BusinessException("Este e-mail já está em uso.");
            });

    user.setName(request.name().trim());
    user.setEmail(email);
    user.setUpdatedAt(LocalDateTime.now());
    return toResponse(userRepository.save(user));
  }

  @Transactional
  public UserProfileResponse updateBusiness(ProfileBusinessRequest request) {
    User user = currentUserProvider.getCurrentUser();

    user.setCompanyName(request.companyName().trim());
    user.setContactEmail(blankToNull(request.contactEmail()));
    user.setContactPhone(blankToNull(request.contactPhone()));
    user.setUpdatedAt(LocalDateTime.now());
    return toResponse(userRepository.save(user));
  }

  @Transactional
  public void deleteAccount() {
    User user = currentUserProvider.getCurrentUser();
    UUID userId = user.getId();
    jdbcTemplate.update(
        "DELETE FROM proposal_items WHERE proposal_id IN (SELECT id FROM proposals WHERE user_id = ?)",
        userId);
    jdbcTemplate.update("DELETE FROM proposals WHERE user_id = ?", userId);
    jdbcTemplate.update("DELETE FROM clients WHERE user_id = ?", userId);
    jdbcTemplate.update("DELETE FROM subscriptions WHERE user_id = ?", userId);
    userRepository.delete(user);
    userRepository.flush();
  }

  private UserProfileResponse toResponse(User user) {
    return new UserProfileResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getPlan(),
        user.getCompanyName(),
        user.getContactEmail(),
        user.getContactPhone());
  }

  private static String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
