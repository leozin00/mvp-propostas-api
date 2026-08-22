package com.mvppropostas.dto.response;

import com.mvppropostas.domain.enums.ProposalStatus;

public record StatusCountResponse(ProposalStatus status, long count) {}
