package com.mvppropostas.dto.response;

import java.math.BigDecimal;

public record AdminMetricsResponse(
    int periodDays,
    long totalUsers,
    long activeUsers,
    long freeUsers,
    long proUsers,
    long newUsers,
    long subscriptions,
    long cancellations,
    BigDecimal mrr,
    String currency,
    long totalProposals,
    long totalClients) {}
