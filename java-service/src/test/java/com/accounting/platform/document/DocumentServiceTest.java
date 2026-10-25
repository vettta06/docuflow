package com.accounting.platform.document;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.accounting.platform.document.dto.ChangeDocumentStatusRequest;
import com.accounting.platform.entity.*;
import com.accounting.platform.repository.*;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock private DocumentRepository documentRepository;
  @Mock private UserRepository userRepository;
  @Mock private AccountantClientRepository accountantClientRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private OperationDocumentRepository operationDocumentRepository;

  @InjectMocks private DocumentService documentService;

  private Document document;
  private User accountant;

  @BeforeEach
  void setUp() {
    Organization org = new Organization();
    org.setId(1L);

    document = new Document();
    document.setId(5L);
    document.setOrganization(org);
    document.setStatus(DocumentStatus.UPLOADED);
    document.setDocumentType(DocumentType.INVOICE); // Добавили тип, чтобы не было NPE
    document.setFileName("test.pdf");

    accountant = new User();
    accountant.setId(20L);
    accountant.setRole(UserRole.ACCOUNTANT);
    accountant.setOrganization(org);
  }

  @Test
  void changeStatus_CannotRevertFromConfirmedToUploaded() {
    document.setStatus(DocumentStatus.CONFIRMED);
    CustomUserDetails accDetails = new CustomUserDetails(accountant);
    ChangeDocumentStatusRequest request =
        new ChangeDocumentStatusRequest("UPLOADED", "Хочу откатить");

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::requireCurrentUser).thenReturn(accDetails);

      when(documentRepository.findById(5L)).thenReturn(Optional.of(document));

      AccountantClient ac = new AccountantClient();
      ac.setOrganization(document.getOrganization());
      when(accountantClientRepository.findByAccountantId(20L)).thenReturn(List.of(ac));

      var ex =
          assertThrows(
              IllegalStateException.class,
              () -> {
                documentService.changeStatus(5L, request);
              });

      assertTrue(ex.getMessage().contains("Недопустимый переход"));
    }
  }

  @Test
  void changeStatus_CanMoveFromUploadedToNeedsReview() {
    CustomUserDetails accDetails = new CustomUserDetails(accountant);
    ChangeDocumentStatusRequest request =
        new ChangeDocumentStatusRequest("NEEDS_REVIEW", "Неверная сумма");

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::requireCurrentUser).thenReturn(accDetails);

      when(documentRepository.findById(5L)).thenReturn(Optional.of(document));
      when(userRepository.findById(20L)).thenReturn(Optional.of(accountant));

      AccountantClient ac = new AccountantClient();
      ac.setOrganization(document.getOrganization());
      when(accountantClientRepository.findByAccountantId(20L)).thenReturn(List.of(ac));

      when(documentRepository.save(org.mockito.ArgumentMatchers.any(Document.class)))
          .thenReturn(document);
      when(operationDocumentRepository.findByDocumentId(5L)).thenReturn(List.of());

      var response = documentService.changeStatus(5L, request);

      assertEquals("NEEDS_REVIEW", response.status());
    }
  }
}
