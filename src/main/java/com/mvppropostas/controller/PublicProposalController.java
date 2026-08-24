package com.mvppropostas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.response.PublicProposalResponse;
import com.mvppropostas.service.PublicProposalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/public/proposals")
@RequiredArgsConstructor
public class PublicProposalController {

  private final PublicProposalService publicProposalService;

  @GetMapping("/{token}")
  PublicProposalResponse getByToken(@PathVariable String token) {
    return publicProposalService.getByToken(token);
  }

  @PostMapping("/{token}/approve")
  PublicProposalResponse approve(@PathVariable String token) {
    return publicProposalService.approve(token);
  }

  @PostMapping("/{token}/reject")
  PublicProposalResponse reject(@PathVariable String token) {
    return publicProposalService.reject(token);
  }
}
