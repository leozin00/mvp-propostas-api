package com.mvppropostas.common.exception;

import java.time.Instant;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Unauthorized",
                "message", exception.getMessage()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException exception) {
    String message =
        exception.getMessage() == null || exception.getMessage().isBlank()
            ? "Acesso negado."
            : exception.getMessage();
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Forbidden",
                "message", message));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", exception.getMessage()));
  }

  @ExceptionHandler(EmailNotVerifiedException.class)
  ResponseEntity<Map<String, Object>> handleEmailNotVerified(EmailNotVerifiedException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Forbidden",
                "code", EmailNotVerifiedException.CODE,
                "message", exception.getMessage()));
  }

  @ExceptionHandler(PlanLimitException.class)
  ResponseEntity<Map<String, Object>> handlePlanLimit(PlanLimitException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Plan Limit",
                "code", exception.getCode(),
                "message", exception.getMessage()));
  }

  @ExceptionHandler(BusinessException.class)
  ResponseEntity<Map<String, Object>> handleBusiness(BusinessException exception) {
    return ResponseEntity.badRequest()
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("Dados inválidos.");

    return ResponseEntity.badRequest()
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation Error",
                "message", message));
  }

  @ExceptionHandler(DataAccessException.class)
  ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException exception) {
    return buildServerError(exception, "Falha ao acessar o banco de dados.");
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
    return buildServerError(exception, "Ocorreu um erro inesperado.");
  }

  private ResponseEntity<Map<String, Object>> buildServerError(
      Exception exception, String message) {
    log.error(message, exception);
    Sentry.captureException(exception);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", message));
  }
}
