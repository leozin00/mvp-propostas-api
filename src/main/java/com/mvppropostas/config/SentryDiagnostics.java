package com.mvppropostas.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SentryDiagnostics {

  @EventListener(ApplicationReadyEvent.class)
  void logSentryStatus() {
    if (Sentry.isEnabled()) {
      log.info("Sentry ativo para o ambiente {}", Sentry.getGlobalScope().getOptions().getEnvironment());
      return;
    }

    log.warn("Sentry desabilitado. Defina SENTRY_DSN para habilitar o monitoramento.");
  }
}
