package com.mvppropostas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.response.BillingCheckoutResponse;
import com.mvppropostas.dto.response.BillingStatusResponse;
import com.mvppropostas.service.BillingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

  private final BillingService billingService;

  @GetMapping
  BillingStatusResponse getStatus() {
    return billingService.getStatus();
  }

  @PostMapping("/checkout")
  BillingCheckoutResponse checkout() {
    return billingService.createCheckout();
  }

  @PostMapping("/sync")
  BillingStatusResponse sync() {
    return billingService.syncCurrentUser();
  }
}
