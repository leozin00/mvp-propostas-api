package com.mvppropostas.dto.request;

import com.mvppropostas.domain.enums.UserPlan;

import jakarta.validation.constraints.NotNull;

public record AdminPlanUpdateRequest(@NotNull UserPlan plan) {}
