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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploaded_by")
  private User uploadedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "document_type", nullable = false, length = 50)
  private DocumentType documentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private DocumentStatus status;

  @Column(name = "file_path", nullable = false, length = 500)
  private String filePath;

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "mime_type", length = 100)
  private String mimeType;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
