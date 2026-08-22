package com.mvppropostas.dto.response;

import java.util.UUID;

import com.mvppropostas.domain.enums.UserPlan;

public record UserProfileResponse(
    UUID id,
    String name,
    String email,
    UserPlan plan,
    String companyName,
    String contactEmail,
    String contactPhone) {}
