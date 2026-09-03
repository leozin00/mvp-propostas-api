package com.mvppropostas.dto.response;

import java.math.BigDecimal;

import com.mvppropostas.domain.enums.UserPlan;

public record BillingStatusResponse(
    UserPlan plan,
    String subscriptionStatus,
    boolean configured,
    boolean sandbox,
    boolean sandboxTestMode,
    BigDecimal amount,
    String currency,
    PlanUsageResponse usage) {}
