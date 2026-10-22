package com.accounting.platform.controller;

import com.accounting.platform.dashboard.DashboardService;
import com.accounting.platform.dashboard.dto.QueueResponse;
import com.accounting.platform.document.dto.DocumentFilter;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountant")
@RequiredArgsConstructor
public class AccountantController {

  private final DashboardService dashboardService;

  @GetMapping("/queue")
  public ResponseEntity<QueueResponse> getQueue(
      @RequestParam(required = false) Long organizationId,
      @RequestParam(required = false) String documentType,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String fileName,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dateTo) {
    DocumentFilter filter =
        new DocumentFilter(
            organizationId, documentType, status, fileName, dateFrom, dateTo, null, null);
    return ResponseEntity.ok(dashboardService.getAccountantQueueFiltered(filter));
  }
}
