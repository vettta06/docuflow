package com.accounting.platform.controller;

import com.accounting.platform.dashboard.DashboardService;
import com.accounting.platform.dashboard.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountant")
@RequiredArgsConstructor
public class AccountantController {

  private final DashboardService dashboardService;

  @GetMapping("/queue")
  public ResponseEntity<QueueResponse> getQueue() {
    return ResponseEntity.ok(dashboardService.getAccountantQueue());
  }
}
