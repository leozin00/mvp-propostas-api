package com.mvppropostas.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyApprovedResponse(
    String month, BigDecimal value, long count, List<RecentProposalResponse> proposals) {}
