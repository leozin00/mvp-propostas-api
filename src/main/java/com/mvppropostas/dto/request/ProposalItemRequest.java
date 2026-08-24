package com.mvppropostas.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProposalItemRequest(
    @NotBlank(message = "Descrição do item é obrigatória") @Size(max = 500) String description,
    @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que zero")
        BigDecimal quantity,
    @NotNull(message = "Valor unitário é obrigatório")
        @DecimalMin(value = "0.00", message = "Valor unitário não pode ser negativo")
        BigDecimal unitPrice) {}
