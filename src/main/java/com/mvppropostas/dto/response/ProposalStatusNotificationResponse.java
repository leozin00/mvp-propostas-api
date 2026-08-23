package com.mvppropostas.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mvppropostas.domain.enums.ProposalStatus;

public record ProposalStatusNotificationResponse(
    UUID proposalId,
    String clientName,
    String title,
    ProposalStatus status,
    LocalDateTime occurredAt) {}
