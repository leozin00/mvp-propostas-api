package com.mvppropostas.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MpWebhookPayload(String type, String action, MpWebhookData data) {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record MpWebhookData(String id) {}
}
