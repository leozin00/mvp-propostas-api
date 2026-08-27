package com.mvppropostas.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.client.MercadoPagoClient;
import com.mvppropostas.dto.mercadopago.MpPreapprovalResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BillingControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private MercadoPagoClient mercadoPagoClient;

  @Test
  void webhookDoesNotRequireAuthentication() throws Exception {
    when(mercadoPagoClient.getPreapproval("preapproval-1"))
        .thenReturn(
            new MpPreapprovalResponse(
                "preapproval-1", "pending", null, null, "not-a-uuid", null, "a@b.com"));

    mockMvc
        .perform(
            post("/api/v1/webhooks/mercadopago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"type":"subscription_preapproval","data":{"id":"preapproval-1"}}
                    """))
        .andExpect(status().isOk());
  }

  @Test
  void billingStatusRequiresAuthentication() throws Exception {
    mockMvc.perform(get("/api/v1/billing")).andExpect(status().isUnauthorized());
  }

  @Test
  void checkoutReturnsMercadoPagoUrl() throws Exception {
    when(mercadoPagoClient.createPreapproval(
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.anyString()))
        .thenReturn(
            new MpPreapprovalResponse(
                "preapproval-checkout",
                "pending",
                "https://www.mercadopago.com.br/subscriptions/checkout",
                "https://sandbox.mercadopago.com.br/subscriptions/checkout",
                "user-ref",
                null,
                "billing@empresa.com"));

    mockMvc
        .perform(
            post("/api/v1/billing/checkout")
                .with(
                    jwt()
                        .jwt(
                            token ->
                                token
                                    .subject("firebase-billing-user")
                                    .claim("email", "billing@empresa.com")
                                    .claim("name", "Billing User"))))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.checkoutUrl")
                .value("https://www.mercadopago.com.br/subscriptions/checkout"));
  }
}
