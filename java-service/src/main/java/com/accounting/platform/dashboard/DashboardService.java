package com.accounting.platform.dashboard;

import com.accounting.platform.dashboard.dto.QueueResponse;
import com.accounting.platform.dashboard.dto.QueueStats;
import com.accounting.platform.document.dto.DocumentFilter;
import com.accounting.platform.document.dto.DocumentResponse;
import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.DocumentStatus;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.DocumentRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final DocumentRepository documentRepository;
  private final UserRepository userRepository;
  private final AccountantClientRepository accountantClientRepository;

  @Transactional(readOnly = true)
  public QueueResponse getAccountantQueueFiltered(DocumentFilter filter) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    List<Document> documents;
    if (current.getRole() == UserRole.MANAGER) {
      documents = documentRepository.findAll();
    } else if (current.getRole() == UserRole.ACCOUNTANT) {
      List<Long> orgIds =
          accountantClientRepository.findByAccountantId(current.getId()).stream()
              .map(ac -> ac.getOrganization().getId())
              .toList();
      documents = orgIds.isEmpty() ? List.of() : documentRepository.findByOrganizationIdIn(orgIds);
    } else {
      throw new IllegalStateException("Доступ только для бухгалтеров и менеджеров");
    }

    if (filter != null) {
      documents =
          documents.stream()
              .filter(
                  d ->
                      filter.organizationId() == null
                          || d.getOrganization().getId().equals(filter.organizationId()))
              .filter(
                  d ->
                      filter.documentType() == null
                          || d.getDocumentType().name().equals(filter.documentType()))
              .filter(d -> filter.status() == null || d.getStatus().name().equals(filter.status()))
              .filter(
                  d ->
                      filter.fileName() == null
                          || d.getFileName()
                              .toLowerCase()
                              .contains(filter.fileName().toLowerCase()))
              .filter(
                  d ->
                      filter.dateFrom() == null
                          || !d.getCreatedAt().toLocalDate().isBefore(filter.dateFrom()))
              .filter(
                  d ->
                      filter.dateTo() == null
                          || !d.getCreatedAt().toLocalDate().isAfter(filter.dateTo()))
              .toList();
    }

    long newDocs = documents.stream().filter(d -> d.getStatus() == DocumentStatus.UPLOADED).count();
    long needsReview =
        documents.stream().filter(d -> d.getStatus() == DocumentStatus.NEEDS_REVIEW).count();
    long waitingClient =
        documents.stream().filter(d -> d.getStatus() == DocumentStatus.WAITING_ADDITIONAL).count();
    long confirmed =
        documents.stream().filter(d -> d.getStatus() == DocumentStatus.CONFIRMED).count();

    QueueStats stats = new QueueStats(newDocs, needsReview, waitingClient, confirmed);

    List<DocumentResponse> docResponses =
        documents.stream().map(doc -> DocumentResponse.from(doc, null)).toList();

    return new QueueResponse(stats, docResponses);
  }
}
