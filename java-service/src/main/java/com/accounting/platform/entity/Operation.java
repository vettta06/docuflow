package com.accounting.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "operations")
@Getter
@Setter
public class Operation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accountant_id")
  private User accountant;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "type_id", nullable = false)
  private OperationType type;

  @Column(nullable = false)
  private String title;

  @Column(name = "total_amount", precision = 15, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "operation_date")
  private LocalDate operationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OperationStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "completeness_status", nullable = false, length = 20)
  private CompletenessStatus completenessStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  private User createdBy;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "accepted_at")
  private LocalDateTime acceptedAt;
}
