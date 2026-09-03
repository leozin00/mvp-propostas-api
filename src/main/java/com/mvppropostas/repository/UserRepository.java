package com.mvppropostas.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvppropostas.domain.entity.User;
import com.mvppropostas.domain.enums.UserPlan;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmailIgnoreCase(String email);

  Optional<User> findByFirebaseUid(String firebaseUid);

  long countByPlan(UserPlan plan);

  long countByCreatedAtGreaterThanEqual(LocalDateTime createdAt);

  long countByLastLoginGreaterThanEqual(LocalDateTime lastLogin);

  long countByLastLoginIsNotNull();

  @Query(
      """
      SELECT u
      FROM User u
      WHERE (:plan IS NULL OR u.plan = :plan)
        AND (
          :query = ''
          OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
          OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
        )
      """)
  Page<User> searchAccounts(
      @Param("query") String query, @Param("plan") UserPlan plan, Pageable pageable);
}
