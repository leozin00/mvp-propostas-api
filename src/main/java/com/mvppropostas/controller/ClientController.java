package com.mvppropostas.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.request.ClientRequest;
import com.mvppropostas.dto.response.ClientResponse;
import com.mvppropostas.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  List<ClientResponse> list() {
    return clientService.list();
  }

  @GetMapping("/{id}")
  ClientResponse getById(@PathVariable UUID id) {
    return clientService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ClientResponse create(@Valid @RequestBody ClientRequest request) {
    return clientService.create(request);
  }

  @PutMapping("/{id}")
  ClientResponse update(@PathVariable UUID id, @Valid @RequestBody ClientRequest request) {
    return clientService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID id) {
    clientService.delete(id);
  }
}
