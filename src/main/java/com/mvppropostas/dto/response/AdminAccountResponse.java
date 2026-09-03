package com.mvppropostas.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mvppropostas.domain.enums.UserPlan;

public record AdminAccountResponse(
    UUID id,
    String name,
    String email,
    UserPlan plan,
    boolean active,
    LocalDateTime createdAt) {}
