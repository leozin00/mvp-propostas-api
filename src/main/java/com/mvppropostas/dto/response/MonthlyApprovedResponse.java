package com.mvppropostas.dto.response;

import java.math.BigDecimal;

public record MonthlyApprovedResponse(String month, BigDecimal value, long count) {}
