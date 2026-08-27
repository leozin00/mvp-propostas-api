package com.mvppropostas.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class MercadoPagoSignatureVerifier {

  public boolean isValid(String secret, String signatureHeader, String requestId, String dataId) {
    if (secret == null || secret.isBlank()) {
      return true;
    }
    if (signatureHeader == null || signatureHeader.isBlank()) {
      return false;
    }

    String timestamp = extractPart(signatureHeader, "ts");
    String provided = extractPart(signatureHeader, "v1");
    if (timestamp == null || provided == null) {
      return false;
    }

    String expected = hmacSha256Hex(secret, buildManifest(dataId, requestId, timestamp));
    return MessageDigest.isEqual(
        expected.getBytes(StandardCharsets.UTF_8), provided.getBytes(StandardCharsets.UTF_8));
  }

  static String buildManifest(String dataId, String requestId, String timestamp) {
    StringBuilder manifest = new StringBuilder();
    if (dataId != null && !dataId.isBlank()) {
      manifest.append("id:").append(normalizeDataId(dataId)).append(';');
    }
    if (requestId != null && !requestId.isBlank()) {
      manifest.append("request-id:").append(requestId).append(';');
    }
    manifest.append("ts:").append(timestamp).append(';');
    return manifest.toString();
  }

  private static String normalizeDataId(String dataId) {
    if (dataId.chars().anyMatch(Character::isLetter)) {
      return dataId.toLowerCase(Locale.ROOT);
    }
    return dataId;
  }

  private static String extractPart(String header, String key) {
    for (String part : header.split(",")) {
      String[] pair = part.trim().split("=", 2);
      if (pair.length == 2 && key.equals(pair[0].trim())) {
        return pair[1].trim();
      }
    }
    return null;
  }

  private static String hmacSha256Hex(String secret, String message) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
      throw new IllegalStateException("Falha ao validar assinatura do webhook.", exception);
    }
  }
}
