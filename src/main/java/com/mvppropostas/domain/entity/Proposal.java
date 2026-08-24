package com.mvppropostas.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mvppropostas.domain.enums.ProposalStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "proposals")
@Getter
@Setter
public class Proposal {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "client_id", nullable = false, insertable = false, updatable = false)
  private UUID clientId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @Column(nullable = false)
  private String title;

  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProposalStatus status;

  @Column(name = "valid_until", nullable = false)
  private LocalDate validUntil;

  @Column(nullable = false)
  private BigDecimal subtotal;

  @Column(nullable = false)
  private BigDecimal discount;

  @Column(nullable = false)
  private BigDecimal total;

  @Column(name = "public_token")
  private String publicToken;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "viewed_at")
  private LocalDateTime viewedAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "rejected_at")
  private LocalDateTime rejectedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id ASC")
  private List<ProposalItem> items = new ArrayList<>();
}
