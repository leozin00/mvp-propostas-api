package com.mvppropostas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mvppropostas.config.MercadoPagoProperties;
import com.mvppropostas.dto.mercadopago.MpWebhookPayload;
import com.mvppropostas.service.BillingService;
import com.mvppropostas.service.MercadoPagoSignatureVerifier;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/webhooks/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

  private final BillingService billingService;
  private final MercadoPagoProperties properties;
  private final MercadoPagoSignatureVerifier signatureVerifier;

  @PostMapping
  void handle(
      @RequestBody(required = false) MpWebhookPayload payload,
      @RequestParam(name = "data.id", required = false) String queryDataId,
      @RequestHeader(name = "x-signature", required = false) String signature,
      @RequestHeader(name = "x-request-id", required = false) String requestId) {
    String dataId =
        queryDataId != null && !queryDataId.isBlank()
            ? queryDataId
            : payload != null && payload.data() != null ? payload.data().id() : null;

    if (!signatureVerifier.isValid(properties.webhookSecret(), signature, requestId, dataId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura do webhook inválida.");
    }

    billingService.handleWebhook(payload, queryDataId);
  }
}
