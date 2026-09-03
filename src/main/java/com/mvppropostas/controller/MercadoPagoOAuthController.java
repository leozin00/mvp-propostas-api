package com.mvppropostas.controller;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.common.exception.BusinessException;
import com.mvppropostas.dto.mercadopago.MpOAuthTokenResponse;
import com.mvppropostas.service.MercadoPagoOAuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dev/mercadopago/oauth")
@ConditionalOnProperty(name = "app.debug.mercadopago-oauth-enabled", havingValue = "true")
@RequiredArgsConstructor
public class MercadoPagoOAuthController {

  private final MercadoPagoOAuthService oauthService;

  @GetMapping("/start")
  void start(HttpServletResponse response) throws IOException {
    response.sendRedirect(oauthService.buildAuthorizationUrl());
  }

  @GetMapping(value = "/callback", produces = MediaType.TEXT_HTML_VALUE)
  String callback(@RequestParam(required = false) String code, @RequestParam(required = false) String state) {
    if (code == null || code.isBlank()) {
      throw new BusinessException("Código de autorização ausente. Tente novamente em /oauth/start.");
    }

    MpOAuthTokenResponse token = oauthService.exchangeAuthorizationCode(code, state);
    if (token == null || token.accessToken() == null || token.accessToken().isBlank()) {
      throw new BusinessException("Mercado Pago não retornou access_token.");
    }

    return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head><meta charset="utf-8"><title>Token sandbox — GestaoPropostas</title></head>
        <body style="font-family:system-ui;max-width:720px;margin:2rem auto;padding:0 1rem;">
          <h1>Token do vendedor de teste</h1>
          <p>Cole no <code>.env</code> do backend e reinicie o serviço:</p>
          <pre style="background:#f4f4f5;padding:1rem;border-radius:8px;overflow:auto;">MERCADOPAGO_SANDBOX_ACCESS_TOKEN=%s</pre>
          <p>Defina também o comprador de teste:</p>
          <pre style="background:#f4f4f5;padding:1rem;border-radius:8px;">MERCADOPAGO_TEST_PAYER_USER_ID=&lt;User ID do comprador&gt;</pre>
          <p>No checkout, entre com <strong>Usuário + Senha</strong> do comprador de teste (janela anônima).</p>
          <p><a href="https://www.mercadopago.com.br/developers/panel/app/4673848288729614/test-users">Abrir contas de teste</a></p>
        </body>
        </html>
        """
        .formatted(token.accessToken());
  }
}
