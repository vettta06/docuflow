package com.accounting.platform.operation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.accounting.platform.entity.*;
import com.accounting.platform.operation.dto.CreateOperationRequest;
import com.accounting.platform.repository.*;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import java.math.BigDecimal;
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
class OperationServiceTest {

  @Mock private OperationRepository operationRepository;
  @Mock private OperationTypeRepository operationTypeRepository;
  @Mock private OrganizationRepository organizationRepository;
  @Mock private UserRepository userRepository;
  @Mock private AccountantClientRepository accountantClientRepository;
  @Mock private RequiredDocumentRepository requiredDocumentRepository;
  @Mock private OperationDocumentRepository operationDocumentRepository;

  @InjectMocks private OperationService operationService;

  private Organization organization;
  private User clientUser;
  private CustomUserDetails clientDetails;
  private OperationType operationType;

  @BeforeEach
  void setUp() {
    organization = new Organization();
    organization.setId(1L);
    organization.setName("ООО Тест");

    clientUser = new User();
    clientUser.setId(10L);
    clientUser.setOrganization(organization);
    clientUser.setRole(UserRole.CLIENT);

    clientDetails = new CustomUserDetails(clientUser);

    operationType = new OperationType();
    operationType.setId(1L);
    operationType.setCode("EQUIPMENT_PURCHASE");
  }

  @Test
  void createOperation_ForClient_SetsOrganizationFromProfile() {
    CreateOperationRequest request =
        new CreateOperationRequest(1L, "Покупка ноутбука", BigDecimal.valueOf(50000), null, null);

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::requireCurrentUser).thenReturn(clientDetails);

      when(userRepository.findById(10L)).thenReturn(Optional.of(clientUser));
      when(operationTypeRepository.findById(1L)).thenReturn(Optional.of(operationType));
      when(operationRepository.save(any(Operation.class)))
          .thenAnswer(
              invocation -> {
                Operation op = invocation.getArgument(0);
                op.setId(100L);
                return op;
              });

      RequiredDocument reqDoc = new RequiredDocument();
      reqDoc.setDocumentType("INVOICE");
      reqDoc.setIsMandatory(true);
      when(requiredDocumentRepository.findByOperationTypeId(1L)).thenReturn(List.of(reqDoc));
      when(operationDocumentRepository.findByOperationId(any())).thenReturn(List.of());

      var response = operationService.create(request);

      assertNotNull(response);
      assertEquals("Покупка ноутбука", response.title());
      assertEquals(1L, response.organizationId());
      assertFalse(response.complete());
    }
  }

  @Test
  void createOperation_ForAccountant_ThrowsExceptionIfNotAssigned() {
    CreateOperationRequest request =
        new CreateOperationRequest(1L, "Закупка", BigDecimal.valueOf(1000), null, 2L);

    User accountant = new User();
    accountant.setId(20L);
    accountant.setRole(UserRole.ACCOUNTANT);
    CustomUserDetails accDetails = new CustomUserDetails(accountant);

    Organization otherOrg = new Organization();
    otherOrg.setId(2L);
    otherOrg.setName("ООО Чужое");

    try (MockedStatic<SecurityUtils> utilities = Mockito.mockStatic(SecurityUtils.class)) {
      utilities.when(SecurityUtils::requireCurrentUser).thenReturn(accDetails);

      when(organizationRepository.findById(2L)).thenReturn(Optional.of(otherOrg));
      when(accountantClientRepository.findByAccountantId(20L)).thenReturn(List.of());

      var ex =
          assertThrows(
              com.accounting.platform.common.InvalidOperationException.class,
              () -> {
                operationService.create(request);
              });

      assertTrue(ex.getMessage().contains("Вы не назначены на организацию"));
    }
  }
}
