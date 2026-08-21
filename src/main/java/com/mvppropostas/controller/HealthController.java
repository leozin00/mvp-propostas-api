package com.mvppropostas.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.response.HealthResponse;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

  @Value("${spring.application.name}")
  private String applicationName;

  @GetMapping
  HealthResponse health() {
    return new HealthResponse("UP", applicationName, "0.0.1-SNAPSHOT");
  }
}
