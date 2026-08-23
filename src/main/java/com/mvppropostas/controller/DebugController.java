package com.mvppropostas.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/debug")
@ConditionalOnProperty(name = "app.debug.sentry-test-enabled", havingValue = "true")
public class DebugController {

  @GetMapping("/sentry")
  void triggerSentryError() {
    throw new IllegalStateException("Sentry test error from MVP Propostas API");
  }
}
