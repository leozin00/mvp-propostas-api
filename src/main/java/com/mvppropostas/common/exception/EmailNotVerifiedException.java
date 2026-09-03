package com.mvppropostas.common.exception;

public class EmailNotVerifiedException extends RuntimeException {

  public static final String CODE = "EMAIL_NOT_VERIFIED";

  public EmailNotVerifiedException() {
    super("Confirme seu e-mail para continuar.");
  }
}
