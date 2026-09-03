package com.mvppropostas.common.exception;

import lombok.Getter;

@Getter
public class PlanLimitException extends BusinessException {

  public static final String CLIENTS = "PLAN_LIMIT_CLIENTS";
  public static final String PROPOSALS = "PLAN_LIMIT_PROPOSALS";

  private final String code;

  public PlanLimitException(String code, String message) {
    super(message);
    this.code = code;
  }
}
