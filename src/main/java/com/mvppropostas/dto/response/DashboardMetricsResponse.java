package com.mvppropostas.dto.response;

import java.math.BigDecimal;

public record DashboardMetricsResponse(
    long totalProposals,
    long inProgress,
    long approved,
    long viewed,
    BigDecimal approvedValue,
    BigDecimal conversionRate) {}
