package com.mvppropostas.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "mp_preapproval_id", nullable = false, unique = true)
  private String mpPreapprovalId;

  @Column(name = "mp_preapproval_plan_id")
  private String mpPreapprovalPlanId;

  @Column(nullable = false)
  private String status;

  @Column(name = "checkout_url")
  private String checkoutUrl;

  @Column(name = "payer_email")
  private String payerEmail;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
