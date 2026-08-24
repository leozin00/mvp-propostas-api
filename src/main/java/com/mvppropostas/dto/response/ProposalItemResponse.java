package com.mvppropostas.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProposalItemResponse(
    UUID id, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal total) {}
