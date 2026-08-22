package com.mvppropostas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.mvppropostas.domain.enums.ProposalStatus;

public record RecentProposalResponse(
    UUID id,
    String clientName,
    String title,
    BigDecimal total,
    ProposalStatus status,
    LocalDate validUntil) {}
