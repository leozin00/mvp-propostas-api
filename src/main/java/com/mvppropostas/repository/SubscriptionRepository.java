package com.mvppropostas.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mvppropostas.domain.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  Optional<Subscription> findByMpPreapprovalId(String mpPreapprovalId);

  Optional<Subscription> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

  long countByCreatedAtGreaterThanEqual(LocalDateTime createdAt);

  long countByUpdatedAtGreaterThanEqualAndStatusNotIgnoreCase(
      LocalDateTime updatedAt, String status);

  long countByStatusNotIgnoreCase(String status);
}
