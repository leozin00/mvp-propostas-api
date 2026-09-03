package com.mvppropostas.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(String emails) {

  public Set<String> allowedEmails() {
    if (emails == null || emails.isBlank()) {
      return Set.of();
    }
    return Arrays.stream(emails.split(","))
        .map(String::trim)
        .filter(email -> !email.isEmpty())
        .map(email -> email.toLowerCase(Locale.ROOT))
        .collect(Collectors.toUnmodifiableSet());
  }
}
