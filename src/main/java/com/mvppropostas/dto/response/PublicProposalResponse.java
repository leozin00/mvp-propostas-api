package com.mvppropostas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.mvppropostas.domain.enums.ProposalStatus;

public record PublicProposalResponse(
    String title,
    String description,
    String clientName,
    ProposalStatus status,
    LocalDate validUntil,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal total,
    String issuerName,
    String referenceCode,
    List<ProposalItemResponse> items) {}
