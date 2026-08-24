package com.mvppropostas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.mvppropostas.domain.enums.ProposalStatus;

public record ProposalResponse(
    UUID id,
    UUID clientId,
    String clientName,
    String clientPhone,
    String title,
    String description,
    ProposalStatus status,
    LocalDate validUntil,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal total,
    String publicToken,
    LocalDate createdAt,
    List<ProposalItemResponse> items) {}
