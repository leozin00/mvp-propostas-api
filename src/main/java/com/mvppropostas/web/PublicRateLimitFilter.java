package com.mvppropostas.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class PublicRateLimitFilter extends OncePerRequestFilter {

  private final int maxRequests;
  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

  public PublicRateLimitFilter(
      @Value("${app.rate-limit.public-per-minute:60}") int maxRequests) {
    this.maxRequests = Math.max(1, maxRequests);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith("/api/v1/public/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String key = clientKey(request);
    long now = System.currentTimeMillis();
    Window window = windows.compute(key, (ignored, current) -> {
      if (current == null || now - current.startedAtMs >= 60_000) {
        return new Window(now);
      }
      current.count.incrementAndGet();
      return current;
    });

    if (window.count.get() > maxRequests) {
      response.setStatus(429);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter()
          .write(
              """
              {"timestamp":"%s","status":429,"error":"Too Many Requests","message":"Muitas consultas neste link. Tente de novo em instantes."}
              """
                  .formatted(Instant.now()));
      return;
    }

    filterChain.doFilter(request, response);
  }

  private static String clientKey(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
  }

  private static final class Window {
    private final long startedAtMs;
    private final AtomicInteger count = new AtomicInteger(1);

    private Window(long startedAtMs) {
      this.startedAtMs = startedAtMs;
    }
  }
}
