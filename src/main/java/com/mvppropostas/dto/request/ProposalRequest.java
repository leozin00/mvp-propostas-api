package com.mvppropostas.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProposalRequest(
    @NotNull(message = "Cliente é obrigatório") UUID clientId,
    @NotBlank(message = "Título é obrigatório") @Size(max = 255) String title,
    String description,
    @NotNull(message = "Validade é obrigatória") LocalDate validUntil,
    @NotNull(message = "Desconto é obrigatório")
        @DecimalMin(value = "0.00", message = "Desconto não pode ser negativo")
        BigDecimal discount,
    @NotEmpty(message = "Inclua pelo menos um item") @Valid List<ProposalItemRequest> items) {}
