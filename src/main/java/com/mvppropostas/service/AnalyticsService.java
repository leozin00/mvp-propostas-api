package com.mvppropostas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvppropostas.domain.entity.Proposal;
import com.mvppropostas.domain.enums.ProposalStatus;
import com.mvppropostas.dto.response.DashboardMetricsResponse;
import com.mvppropostas.dto.response.DashboardResponse;
import com.mvppropostas.dto.response.MonthlyApprovedResponse;
import com.mvppropostas.dto.response.ProfileAnalyticsResponse;
import com.mvppropostas.dto.response.ProfileAnalyticsSummaryResponse;
import com.mvppropostas.dto.response.RecentProposalResponse;
import com.mvppropostas.dto.response.StatusCountResponse;
import com.mvppropostas.repository.ClientRepository;
import com.mvppropostas.repository.ProposalRepository;
import com.mvppropostas.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

  private static final List<ProposalStatus> IN_PROGRESS_STATUSES =
      List.of(ProposalStatus.DRAFT, ProposalStatus.SENT, ProposalStatus.VIEWED);

  private static final List<ProposalStatus> SENT_STATUSES =
      List.of(
          ProposalStatus.SENT,
          ProposalStatus.VIEWED,
          ProposalStatus.APPROVED,
          ProposalStatus.REJECTED,
          ProposalStatus.EXPIRED);

  private final ProposalRepository proposalRepository;
  private final ClientRepository clientRepository;
  private final CurrentUserProvider currentUserProvider;

  public DashboardResponse getDashboard() {
    UUID userId = currentUserProvider.getCurrentUserId();

    long total = proposalRepository.countByUserIdAndDeletedAtIsNull(userId);
    long inProgress =
        proposalRepository.countByUserIdAndStatusInAndDeletedAtIsNull(userId, IN_PROGRESS_STATUSES);
    long approved =
        proposalRepository.countByUserIdAndStatusAndDeletedAtIsNull(userId, ProposalStatus.APPROVED);
    long viewed =
        proposalRepository.countByUserIdAndStatusAndDeletedAtIsNull(userId, ProposalStatus.VIEWED);
    BigDecimal approvedValue = proposalRepository.sumApprovedTotalByUserId(userId);
    BigDecimal conversionRate = calculateConversionRate(userId);

    DashboardMetricsResponse metrics =
        new DashboardMetricsResponse(
            total, inProgress, approved, viewed, approvedValue, conversionRate);

    List<RecentProposalResponse> recentProposals =
        proposalRepository.findRecentByUserId(userId, PageRequest.of(0, 5)).stream()
            .map(this::toRecentProposal)
            .toList();

    return new DashboardResponse(metrics, recentProposals);
  }

  public ProfileAnalyticsResponse getProfileAnalytics() {
    UUID userId = currentUserProvider.getCurrentUserId();

    long totalProposals = proposalRepository.countByUserIdAndDeletedAtIsNull(userId);
    long totalClients = clientRepository.countByUserId(userId);
    BigDecimal totalProposed = proposalRepository.sumTotalProposedByUserId(userId);
    BigDecimal totalApproved = proposalRepository.sumApprovedTotalByUserId(userId);
    BigDecimal conversionRate = calculateConversionRate(userId);

    ProfileAnalyticsSummaryResponse summary =
        new ProfileAnalyticsSummaryResponse(
            totalProposals, totalClients, totalProposed, totalApproved, conversionRate);

    Map<ProposalStatus, Long> statusCounts = new EnumMap<>(ProposalStatus.class);
    Arrays.stream(ProposalStatus.values()).forEach(status -> statusCounts.put(status, 0L));

    for (Object[] row : proposalRepository.countByStatusForUser(userId)) {
      statusCounts.put((ProposalStatus) row[0], (Long) row[1]);
    }

    List<StatusCountResponse> byStatus =
        statusCounts.entrySet().stream()
            .map(entry -> new StatusCountResponse(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(entry -> entry.status().name()))
            .toList();

    List<MonthlyApprovedResponse> monthlyApproved = buildMonthlyApproved(userId);

    return new ProfileAnalyticsResponse(summary, byStatus, monthlyApproved);
  }

  private BigDecimal calculateConversionRate(UUID userId) {
    long sentCount =
        proposalRepository.countByUserIdAndStatusInAndDeletedAtIsNull(userId, SENT_STATUSES);
    long approvedCount =
        proposalRepository.countByUserIdAndStatusAndDeletedAtIsNull(userId, ProposalStatus.APPROVED);

    if (sentCount == 0) {
      return BigDecimal.ZERO;
    }

    return BigDecimal.valueOf(approvedCount)
        .multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(sentCount), 1, RoundingMode.HALF_UP);
  }

  private List<MonthlyApprovedResponse> buildMonthlyApproved(UUID userId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    Map<String, MonthlyAccumulator> grouped =
        proposalRepository.findApprovedByUserId(userId).stream()
            .collect(
                Collectors.groupingBy(
                    proposal -> proposal.getApprovedAt().format(formatter),
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        proposals -> {
                          BigDecimal value =
                              proposals.stream()
                                  .map(Proposal::getTotal)
                                  .reduce(BigDecimal.ZERO, BigDecimal::add);
                          return new MonthlyAccumulator(value, proposals.size());
                        })));

    return grouped.entrySet().stream()
        .sorted(Map.Entry.<String, MonthlyAccumulator>comparingByKey().reversed())
        .limit(6)
        .map(
            entry ->
                new MonthlyApprovedResponse(
                    entry.getKey(), entry.getValue().value(), entry.getValue().count()))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private RecentProposalResponse toRecentProposal(Proposal proposal) {
    return new RecentProposalResponse(
        proposal.getId(),
        proposal.getClient().getName(),
        proposal.getTitle(),
        proposal.getTotal(),
        proposal.getStatus(),
        proposal.getValidUntil());
  }

  private record MonthlyAccumulator(BigDecimal value, long count) {}
}
