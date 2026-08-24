package com.mvppropostas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileBusinessRequest(
    @NotBlank(message = "Nome da empresa é obrigatório") @Size(max = 255) String companyName,
    @Email(message = "Informe um e-mail válido") @Size(max = 255) String contactEmail,
    @Size(max = 50) String contactPhone) {}
