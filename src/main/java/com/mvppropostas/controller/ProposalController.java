package com.mvppropostas.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.request.ProposalRequest;
import com.mvppropostas.dto.response.ProposalResponse;
import com.mvppropostas.service.ProposalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proposals")
@RequiredArgsConstructor
public class ProposalController {

  private final ProposalService proposalService;

  @GetMapping
  List<ProposalResponse> list(@RequestParam(required = false) UUID clientId) {
    return proposalService.list(clientId);
  }

  @GetMapping("/{id}")
  ProposalResponse getById(@PathVariable UUID id) {
    return proposalService.getById(id);
  }

  @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
    byte[] pdf = proposalService.exportPdf(id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proposta.pdf\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ProposalResponse create(@Valid @RequestBody ProposalRequest request) {
    return proposalService.create(request);
  }

  @PutMapping("/{id}")
  ProposalResponse update(@PathVariable UUID id, @Valid @RequestBody ProposalRequest request) {
    return proposalService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID id) {
    proposalService.delete(id);
  }

  @PostMapping("/{id}/publish")
  ProposalResponse publish(@PathVariable UUID id) {
    return proposalService.publish(id);
  }

  @PostMapping("/{id}/duplicate")
  @ResponseStatus(HttpStatus.CREATED)
  ProposalResponse duplicate(@PathVariable UUID id) {
    return proposalService.duplicate(id);
  }
}
