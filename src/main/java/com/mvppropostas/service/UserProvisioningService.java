package com.mvppropostas.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.common.exception.UnauthorizedException;
import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProvisioningService {

  private final UserRepository userRepository;

  @Transactional
  public User resolve(Jwt jwt) {
    String firebaseUid = jwt.getSubject();
    if (firebaseUid == null || firebaseUid.isBlank()) {
      throw new UnauthorizedException("Token Firebase sem identificador de usuário.");
    }

    try {
      User user =
          userRepository
              .findByFirebaseUid(firebaseUid)
              .orElseGet(() -> linkOrCreate(firebaseUid, jwt));
      if (!user.isActive()) {
        throw new AccessDeniedException("Esta conta está desativada.");
      }
      return user;
    } catch (DataIntegrityViolationException exception) {
      return userRepository
          .findByFirebaseUid(firebaseUid)
          .orElseThrow(() -> exception);
    }
  }

  private User linkOrCreate(String firebaseUid, Jwt jwt) {
    String email = requiredEmail(jwt);

    return userRepository
        .findByEmailIgnoreCase(email)
        .map(existing -> link(existing, firebaseUid))
        .orElseGet(() -> create(firebaseUid, email, jwt));
  }

  private User link(User existing, String firebaseUid) {
    if (existing.getFirebaseUid() != null && !existing.getFirebaseUid().equals(firebaseUid)) {
      throw new UnauthorizedException("Este e-mail já está vinculado a outra conta.");
    }
    existing.setFirebaseUid(firebaseUid);
    existing.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(existing);
  }

  private User create(String firebaseUid, String email, Jwt jwt) {
    LocalDateTime now = LocalDateTime.now();
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setFirebaseUid(firebaseUid);
    user.setName(resolveName(jwt, email));
    user.setEmail(email);
    user.setPlan(UserPlan.FREE);
    user.setActive(true);
    user.setCreatedAt(now);
    user.setUpdatedAt(now);
    return userRepository.save(user);
  }

  private static String requiredEmail(Jwt jwt) {
    String email = jwt.getClaimAsString("email");
    if (email == null || email.isBlank()) {
      throw new UnauthorizedException("Token Firebase sem e-mail.");
    }
    return email.trim().toLowerCase();
  }

  private static String resolveName(Jwt jwt, String email) {
    String name = jwt.getClaimAsString("name");
    if (name != null && !name.isBlank()) {
      return name.trim();
    }
    int at = email.indexOf('@');
    return at > 0 ? email.substring(0, at) : email;
  }
}
