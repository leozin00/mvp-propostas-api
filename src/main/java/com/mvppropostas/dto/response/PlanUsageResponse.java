package com.mvppropostas.dto.response;

public record PlanUsageResponse(
    int clientsUsed,
    Integer clientsLimit,
    boolean clientsLimitReached,
    int proposalsUsedThisCycle,
    Integer proposalsLimit,
    boolean proposalsLimitReached) {}
