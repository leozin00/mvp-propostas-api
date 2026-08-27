package com.mvppropostas.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MpAuthorizedPaymentResponse(
    String id, String status, @JsonProperty("preapproval_id") String preapprovalId) {}
