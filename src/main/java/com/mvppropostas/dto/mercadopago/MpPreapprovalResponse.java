package com.mvppropostas.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MpPreapprovalResponse(
    String id,
    String status,
    @JsonProperty("init_point") String initPoint,
    @JsonProperty("sandbox_init_point") String sandboxInitPoint,
    @JsonProperty("external_reference") String externalReference,
    @JsonProperty("preapproval_plan_id") String preapprovalPlanId,
    @JsonProperty("payer_email") String payerEmail) {

  public String checkoutUrl() {
    // Homologação e produção usam init_point. sandbox_init_point está depreciado pelo Mercado Pago.
    if (initPoint != null && !initPoint.isBlank()) {
      return initPoint;
    }
    return sandboxInitPoint;
  }
}
