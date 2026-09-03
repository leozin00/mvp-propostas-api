package com.mvppropostas.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mvppropostas.domain.enums.UserPlan;
import com.mvppropostas.dto.request.AdminPlanUpdateRequest;
import com.mvppropostas.dto.response.AdminAccountResponse;
import com.mvppropostas.dto.response.AdminAccountsPageResponse;
import com.mvppropostas.dto.response.AdminMetricsResponse;
import com.mvppropostas.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/metrics")
  AdminMetricsResponse getMetrics(@RequestParam(name = "periodDays", defaultValue = "30") int periodDays) {
    return adminService.getMetrics(periodDays);
  }

  @GetMapping("/accounts")
  AdminAccountsPageResponse listAccounts(
      @RequestParam(name = "q", required = false) String query,
      @RequestParam(name = "plan", required = false) UserPlan plan,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
      @RequestParam(name = "dir", defaultValue = "desc") String direction) {
    return adminService.listAccounts(query, plan, page, size, sort, direction);
  }

  @PutMapping("/accounts/{id}/plan")
  AdminAccountResponse changePlan(
      @PathVariable("id") UUID id, @Valid @RequestBody AdminPlanUpdateRequest request) {
    return adminService.changePlan(id, request.plan());
  }

  @PostMapping("/accounts/{id}/cancel")
  AdminAccountResponse cancelAccount(@PathVariable("id") UUID id) {
    return adminService.cancelAccount(id);
  }
}
