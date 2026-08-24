package com.mvppropostas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
    @NotBlank(message = "Nome é obrigatório") @Size(max = 255) String name,
    @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 50) String document,
    @Size(max = 255) String companyName) {}
