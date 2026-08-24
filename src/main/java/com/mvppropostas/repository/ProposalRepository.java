package com.mvppropostas.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.enums.ProposalStatus;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

  long countByUserIdAndDeletedAtIsNull(UUID userId);

  long countByUserIdAndStatusInAndDeletedAtIsNull(UUID userId, List<ProposalStatus> statuses);

  long countByUserIdAndStatusAndDeletedAtIsNull(UUID userId, ProposalStatus status);

  @Query(
      """
      SELECT COALESCE(SUM(p.total), 0)
      FROM Proposal p
      WHERE p.userId = :userId
        AND p.status = com.mvppropostas.domain.enums.ProposalStatus.APPROVED
        AND p.deletedAt IS NULL
      """)
  BigDecimal sumApprovedTotalByUserId(@Param("userId") UUID userId);

  @Query(
      """
      SELECT COALESCE(SUM(p.total), 0)
      FROM Proposal p
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      """)
  BigDecimal sumTotalProposedByUserId(@Param("userId") UUID userId);

  @Query(
      """
      SELECT p.status, COUNT(p)
      FROM Proposal p
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      GROUP BY p.status
      """)
  List<Object[]> countByStatusForUser(@Param("userId") UUID userId);

  @Query(
      """
      SELECT p
      FROM Proposal p
      JOIN FETCH p.client
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      ORDER BY p.updatedAt DESC
      """)
  List<Proposal> findRecentByUserId(
      @Param("userId") UUID userId, org.springframework.data.domain.Pageable pageable);

  @Query(
      """
      SELECT p
      FROM Proposal p
      WHERE p.userId = :userId
        AND p.status = com.mvppropostas.domain.enums.ProposalStatus.APPROVED
        AND p.approvedAt IS NOT NULL
        AND p.deletedAt IS NULL
      ORDER BY p.approvedAt DESC
      """)
  List<Proposal> findApprovedByUserId(@Param("userId") UUID userId);

  @Query(
      """
      SELECT p
      FROM Proposal p
      JOIN FETCH p.client
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      """)
  List<Proposal> findAllByUserIdWithClient(@Param("userId") UUID userId);

  @Query(
      """
      SELECT p
      FROM Proposal p
      JOIN FETCH p.client
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      ORDER BY p.updatedAt DESC
      """)
  List<Proposal> findVisibleByUserId(@Param("userId") UUID userId);

  @Query(
      """
      SELECT p
      FROM Proposal p
      JOIN FETCH p.client
      WHERE p.userId = :userId
        AND p.clientId = :clientId
        AND p.deletedAt IS NULL
      ORDER BY p.updatedAt DESC
      """)
  List<Proposal> findVisibleByUserIdAndClientId(
      @Param("userId") UUID userId, @Param("clientId") UUID clientId);

  @Query(
      """
      SELECT p
      FROM Proposal p
      JOIN FETCH p.client
      LEFT JOIN FETCH p.items
      WHERE p.id = :id
        AND p.userId = :userId
        AND p.deletedAt IS NULL
      """)
  Optional<Proposal> findVisibleByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

  boolean existsByClientIdAndDeletedAtIsNull(UUID clientId);

  long countByClientIdAndDeletedAtIsNull(UUID clientId);

  @Query(
      """
      SELECT p.clientId, COUNT(p)
      FROM Proposal p
      WHERE p.userId = :userId
        AND p.deletedAt IS NULL
      GROUP BY p.clientId
      """)
  List<Object[]> countVisibleByClientForUser(@Param("userId") UUID userId);
}
