package com.mvppropostas.dto.response;

import java.util.List;

public record ProfileAnalyticsResponse(
    ProfileAnalyticsSummaryResponse summary,
    List<StatusCountResponse> byStatus,
    List<MonthlyApprovedResponse> monthlyApproved) {}
