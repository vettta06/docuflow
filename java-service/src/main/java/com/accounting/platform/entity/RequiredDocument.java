package com.accounting.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "required_documents",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_required_doc",
            columnNames = {"operation_type_id", "document_type"}))
@Getter
@Setter
public class RequiredDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "operation_type_id", nullable = false)
  private OperationType operationType;

  @Column(name = "document_type", nullable = false, length = 50)
  private String documentType;

  @Column(name = "is_mandatory", nullable = false)
  private Boolean isMandatory;
}
