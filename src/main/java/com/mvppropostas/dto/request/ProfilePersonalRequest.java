package com.mvppropostas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfilePersonalRequest(
    @NotBlank(message = "Nome é obrigatório") @Size(max = 255) String name,
    @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 255)
        String email) {}
