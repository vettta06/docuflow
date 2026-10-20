package com.accounting.platform.document;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.document.dto.ChangeDocumentStatusRequest;
import com.accounting.platform.document.dto.DocumentResponse;
import com.accounting.platform.entity.AccountantClient;
import com.accounting.platform.entity.Comment;
import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.DocumentStatus;
import com.accounting.platform.entity.DocumentType;
import com.accounting.platform.entity.Operation;
import com.accounting.platform.entity.OperationDocument;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.grpc.ProcessResponse;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.CommentRepository;
import com.accounting.platform.repository.DocumentRepository;
import com.accounting.platform.repository.OperationDocumentRepository;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import com.accounting.platform.storage.FileStorageService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

  private final DocumentRepository documentRepository;
  private final OperationRepository operationRepository;
  private final OperationDocumentRepository operationDocumentRepository;
  private final UserRepository userRepository;
  private final AccountantClientRepository accountantClientRepository;
  private final FileStorageService fileStorageService;
  private final CommentRepository commentRepository;
  private final DocumentProcessorClient documentProcessorClient;
  private final com.accounting.platform.repository.DocumentDataRepository documentDataRepository;

  private static final Map<DocumentStatus, Set<DocumentStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          DocumentStatus.UPLOADED,
              Set.of(
                  DocumentStatus.NEEDS_REVIEW, DocumentStatus.CONFIRMED, DocumentStatus.REJECTED),
          DocumentStatus.NEEDS_REVIEW,
              Set.of(DocumentStatus.UPLOADED, DocumentStatus.CONFIRMED, DocumentStatus.REJECTED),
          DocumentStatus.REJECTED, Set.of(DocumentStatus.UPLOADED));

  @Transactional
  public DocumentResponse upload(MultipartFile file, Long operationId, String documentTypeStr) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    Operation operation =
        operationRepository
            .findById(operationId)
            .orElseThrow(
                () -> new NotFoundException("Операция с id " + operationId + " не найдена"));

    checkAccessToOperation(operation, current);

    DocumentType documentType = parseDocumentType(documentTypeStr);

    String storedName = null;
    try {
      storedName = fileStorageService.store(file);

      Document doc = new Document();
      doc.setOrganization(operation.getOrganization());
      doc.setUploadedBy(userRepository.getReferenceById(current.getId()));
      doc.setDocumentType(documentType);
      doc.setStatus(DocumentStatus.UPLOADED);
      doc.setFilePath(storedName);
      doc.setFileName(file.getOriginalFilename());
      doc.setFileSize(file.getSize());
      doc.setMimeType(file.getContentType());
      doc.setCreatedAt(LocalDateTime.now());
      doc.setUpdatedAt(LocalDateTime.now());

      Document saved = documentRepository.save(doc);

      OperationDocument link = new OperationDocument();
      link.setOperation(operation);
      link.setDocument(saved);
      link.setAddedAt(LocalDateTime.now());
      operationDocumentRepository.save(link);

      try {
        ProcessResponse ocrResult =
            documentProcessorClient.processDocument(
                file.getBytes(), file.getOriginalFilename(), documentType.name(), saved.getId());
        log.info(
            "Python-сервис ответил: success={}, type={}, number={}, amount={}",
            ocrResult.getSuccess(),
            ocrResult.getDocumentType(),
            ocrResult.getNumber(),
            ocrResult.getAmount());

        if (ocrResult.getSuccess()) {
          com.accounting.platform.entity.DocumentData data =
              new com.accounting.platform.entity.DocumentData();
          data.setDocument(saved);
          data.setNumber(ocrResult.getNumber().isEmpty() ? null : ocrResult.getNumber());
          if (!ocrResult.getDate().isEmpty()) {
            try {
              data.setDate(java.time.LocalDate.parse(ocrResult.getDate()));
            } catch (Exception ignored) {
            }
          }
          data.setSellerInn(ocrResult.getInn().isEmpty() ? null : ocrResult.getInn());
          data.setAmount(java.math.BigDecimal.valueOf(ocrResult.getAmount()));
          data.setCreatedAt(java.time.LocalDateTime.now());

          documentDataRepository.save(data);
          log.info("Данные из Python сохранены в document_data для документа {}", saved.getId());
        }

      } catch (Exception e) {
        log.error("Не удалось получить данные от Python-сервиса, но документ сохранен.", e);
      }

      return DocumentResponse.from(saved, operation.getId());

    } catch (RuntimeException ex) {
      if (storedName != null) {
        fileStorageService.delete(storedName);
      }
      throw ex;
    }
  }

  private DocumentType parseDocumentType(String raw) {
    if (raw == null || raw.isBlank()) {
      throw new InvalidOperationException("Тип документа обязателен");
    }
    try {
      return DocumentType.valueOf(raw.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidOperationException("Недопустимый тип документа: " + raw);
    }
  }

  private void checkAccessToOperation(Operation op, CustomUserDetails current) {
    Long orgId = op.getOrganization().getId();

    switch (current.getRole()) {
      case CLIENT -> {
        User user =
            userRepository
                .findById(current.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getOrganization() == null || !user.getOrganization().getId().equals(orgId)) {
          throw new NotFoundException("Операция с id " + op.getId() + " не найдена");
        }
      }
      case ACCOUNTANT -> {
        List<AccountantClient> assignments =
            accountantClientRepository.findByAccountantId(current.getId());
        boolean assigned =
            assignments.stream().anyMatch(ac -> ac.getOrganization().getId().equals(orgId));
        if (!assigned) {
          throw new NotFoundException("Операция с id " + op.getId() + " не найдена");
        }
      }
      case MANAGER -> {}
    }
  }

  @Transactional(readOnly = true)
  public List<DocumentResponse> list() {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    List<Document> documents =
        switch (current.getRole()) {
          case CLIENT -> {
            User user =
                userRepository
                    .findById(current.getId())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
            if (user.getOrganization() == null) {
              yield List.of();
            }
            yield documentRepository.findByOrganizationId(user.getOrganization().getId());
          }
          case ACCOUNTANT -> {
            List<Long> orgIds =
                accountantClientRepository.findByAccountantId(current.getId()).stream()
                    .map(ac -> ac.getOrganization().getId())
                    .toList();
            yield orgIds.isEmpty() ? List.of() : documentRepository.findByOrganizationIdIn(orgIds);
          }
          case MANAGER -> documentRepository.findAll();
        };

    return documents.stream().map(doc -> DocumentResponse.from(doc, findOperationId(doc))).toList();
  }

  private Long findOperationId(Document doc) {
    return operationDocumentRepository.findByDocumentId(doc.getId()).stream()
        .findFirst()
        .map(od -> od.getOperation().getId())
        .orElse(null);
  }

  @Transactional(readOnly = true)
  public DocumentResponse getById(Long id) {
    Document doc =
        documentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Документ с id " + id + " не найден"));

    checkAccessToOrganization(doc.getOrganization().getId());
    return DocumentResponse.from(doc, findOperationId(doc));
  }

  private void checkAccessToOrganization(Long orgId) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    switch (current.getRole()) {
      case CLIENT -> {
        User user =
            userRepository
                .findById(current.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getOrganization() == null || !user.getOrganization().getId().equals(orgId)) {
          throw new NotFoundException("Документ не найден");
        }
      }
      case ACCOUNTANT -> {
        boolean assigned =
            accountantClientRepository.findByAccountantId(current.getId()).stream()
                .anyMatch(ac -> ac.getOrganization().getId().equals(orgId));
        if (!assigned) {
          throw new NotFoundException("Документ не найден");
        }
      }
      case MANAGER -> {}
    }
  }

  @Transactional(readOnly = true)
  public Resource download(Long id) {
    Document doc =
        documentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Документ с id " + id + " не найден"));

    checkAccessToOrganization(doc.getOrganization().getId());
    return fileStorageService.load(doc.getFilePath());
  }

  @Transactional
  public DocumentResponse changeStatus(Long id, ChangeDocumentStatusRequest request) {
    Document doc =
        documentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Документ с id " + id + " не найден"));

    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    if (current.getRole() == UserRole.CLIENT) {
      throw new InvalidOperationException("Клиент не может менять статус документа");
    }

    checkAccessToOrganization(doc.getOrganization().getId());

    DocumentStatus newStatus;
    try {
      newStatus = DocumentStatus.valueOf(request.status().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidOperationException("Недопустимый статус: " + request.status());
    }

    Set<DocumentStatus> allowedNext = ALLOWED_TRANSITIONS.getOrDefault(doc.getStatus(), Set.of());
    if (!allowedNext.contains(newStatus)) {
      throw new IllegalStateException(
          "Недопустимый переход из " + doc.getStatus() + " в " + newStatus);
    }

    if ((newStatus == DocumentStatus.NEEDS_REVIEW || newStatus == DocumentStatus.REJECTED)
        && (request.comment() == null || request.comment().isBlank())) {
      throw new InvalidOperationException("Для статуса " + newStatus + " обязателен комментарий");
    }

    if (request.comment() != null && !request.comment().isBlank()) {
      Comment comment = new Comment();
      comment.setAuthor(
          userRepository
              .findById(current.getId())
              .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
      comment.setDocument(doc);
      comment.setText(request.comment());
      comment.setCreatedAt(LocalDateTime.now());
      commentRepository.save(comment);
    }

    doc.setStatus(newStatus);
    doc.setUpdatedAt(LocalDateTime.now());
    documentRepository.save(doc);

    return DocumentResponse.from(doc, findOperationId(doc));
  }
}
