package com.mvppropostas.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ClientResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String document,
    String companyName,
    LocalDate createdAt,
    long proposalsCount) {}
