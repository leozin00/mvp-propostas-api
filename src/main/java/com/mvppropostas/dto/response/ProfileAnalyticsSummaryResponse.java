package com.mvppropostas.dto.response;

import java.math.BigDecimal;

public record ProfileAnalyticsSummaryResponse(
    long totalProposals,
    long totalClients,
    BigDecimal totalProposedValue,
    BigDecimal totalApprovedValue,
    BigDecimal conversionRate) {}
