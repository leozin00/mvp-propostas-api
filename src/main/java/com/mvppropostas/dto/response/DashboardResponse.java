package com.mvppropostas.dto.response;

import java.util.List;

public record DashboardResponse(
    DashboardMetricsResponse metrics, List<RecentProposalResponse> recentProposals) {}
