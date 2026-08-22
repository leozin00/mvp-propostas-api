package com.mvppropostas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.dto.response.DashboardResponse;
import com.mvppropostas.dto.response.ProfileAnalyticsResponse;
import com.mvppropostas.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/dashboard")
  DashboardResponse getDashboard() {
    return analyticsService.getDashboard();
  }

  @GetMapping("/profile")
  ProfileAnalyticsResponse getProfileAnalytics() {
    return analyticsService.getProfileAnalytics();
  }
}
