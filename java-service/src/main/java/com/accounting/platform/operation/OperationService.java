package com.accounting.platform.operation;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.entity.CompletenessStatus;
import com.accounting.platform.entity.Operation;
import com.accounting.platform.entity.OperationDocument;
import com.accounting.platform.entity.OperationStatus;
import com.accounting.platform.entity.OperationType;
import com.accounting.platform.entity.Organization;
import com.accounting.platform.entity.RequiredDocument;
import com.accounting.platform.entity.User;
import com.accounting.platform.operation.dto.CreateOperationRequest;
import com.accounting.platform.operation.dto.OperationResponse;
import com.accounting.platform.operation.dto.RequiredDocumentStatus;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.OperationDocumentRepository;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.OperationTypeRepository;
import com.accounting.platform.repository.OrganizationRepository;
import com.accounting.platform.repository.RequiredDocumentRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationService {

  private final OperationRepository operationRepository;
  private final OperationTypeRepository operationTypeRepository;
  private final OrganizationRepository organizationRepository;
  private final UserRepository userRepository;
  private final AccountantClientRepository accountantClientRepository;
  private final RequiredDocumentRepository requiredDocumentRepository;
  private final OperationDocumentRepository operationDocumentRepository;

  @Transactional
  public OperationResponse create(CreateOperationRequest request) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    Organization organization = resolveOrganization(current, request.organizationId());

    OperationType type =
        operationTypeRepository
            .findById(request.typeId())
            .orElseThrow(
                () ->
                    new NotFoundException("Тип операции с id " + request.typeId() + " не найден"));

    User creator =
        userRepository
            .findById(current.getId())
            .orElseThrow(
                () -> new NotFoundException("Пользователь с id " + current.getId() + " не найден"));

    Operation op = new Operation();
    op.setOrganization(organization);
    op.setType(type);
    op.setTitle(request.title());
    op.setTotalAmount(request.totalAmount());
    op.setOperationDate(request.operationDate());
    op.setStatus(OperationStatus.NEW);
    op.setCompletenessStatus(CompletenessStatus.INCOMPLETE);
    op.setCreatedBy(creator);
    op.setCreatedAt(LocalDateTime.now());

    Operation saved = operationRepository.save(op);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<OperationResponse> list() {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    List<Operation> operations =
        switch (current.getRole()) {
          case CLIENT -> {
            User user =
                userRepository
                    .findById(current.getId())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
            if (user.getOrganization() == null) {
              yield List.of();
            }
            yield operationRepository.findByOrganizationId(user.getOrganization().getId());
          }
          case ACCOUNTANT -> {
            List<Long> orgIds =
                accountantClientRepository.findByAccountantId(current.getId()).stream()
                    .map(ac -> ac.getOrganization().getId())
                    .toList();
            yield orgIds.isEmpty() ? List.of() : operationRepository.findByOrganizationIdIn(orgIds);
          }
          case MANAGER -> operationRepository.findAll();
        };

    return operations.stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OperationResponse getById(Long id) {
    Operation op =
        operationRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Операция с id " + id + " не найдена"));

    checkAccess(op);
    return toResponse(op);
  }

  private Organization resolveOrganization(CustomUserDetails current, Long requestedOrgId) {
    return switch (current.getRole()) {
      case CLIENT -> {
        User user =
            userRepository
                .findById(current.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getOrganization() == null) {
          throw new InvalidOperationException(
              "У вас нет привязанной организации. Обратитесь к менеджеру.");
        }
        yield user.getOrganization();
      }
      case ACCOUNTANT -> {
        if (requestedOrgId == null) {
          throw new InvalidOperationException("organizationId обязателен для бухгалтера");
        }
        Organization org =
            organizationRepository
                .findById(requestedOrgId)
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Организация с id " + requestedOrgId + " не найдена"));

        boolean assigned =
            accountantClientRepository.findByAccountantId(current.getId()).stream()
                .anyMatch(ac -> ac.getOrganization().getId().equals(org.getId()));

        if (!assigned) {
          throw new InvalidOperationException("Вы не назначены на организацию " + org.getName());
        }
        yield org;
      }
      case MANAGER -> {
        if (requestedOrgId == null) {
          throw new InvalidOperationException("organizationId обязателен для менеджера");
        }
        yield organizationRepository
            .findById(requestedOrgId)
            .orElseThrow(
                () -> new NotFoundException("Организация с id " + requestedOrgId + " не найдена"));
      }
    };
  }

  private OperationResponse toResponse(Operation op) {
    List<RequiredDocumentStatus> required = buildRequiredStatuses(op);
    boolean complete =
        required.stream()
            .filter(RequiredDocumentStatus::mandatory)
            .allMatch(RequiredDocumentStatus::provided);

    return OperationResponse.from(op, complete, required);
  }

  private List<RequiredDocumentStatus> buildRequiredStatuses(Operation op) {
    List<RequiredDocument> requiredDocs =
        requiredDocumentRepository.findByOperationTypeId(op.getType().getId());

    List<OperationDocument> attached = operationDocumentRepository.findByOperationId(op.getId());

    List<String> attachedTypes =
        attached.stream().map(od -> od.getDocument().getDocumentType().name()).toList();

    List<RequiredDocumentStatus> result = new ArrayList<>();
    for (RequiredDocument rd : requiredDocs) {
      boolean provided = attachedTypes.contains(rd.getDocumentType());
      result.add(new RequiredDocumentStatus(rd.getDocumentType(), rd.getIsMandatory(), provided));
    }
    return result;
  }

  private void checkAccess(Operation op) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();
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
        boolean assigned =
            accountantClientRepository.findByAccountantId(current.getId()).stream()
                .anyMatch(ac -> ac.getOrganization().getId().equals(orgId));
        if (!assigned) {
          throw new NotFoundException("Операция с id " + op.getId() + " не найдена");
        }
      }
      case MANAGER -> {}
    }
  }

  @Transactional
  public void recalculateCompleteness(Operation op) {
    List<RequiredDocumentStatus> required = buildRequiredStatuses(op);
    boolean allProvided =
        required.stream()
            .filter(RequiredDocumentStatus::mandatory)
            .allMatch(RequiredDocumentStatus::provided);
    CompletenessStatus newCompleteness =
        allProvided ? CompletenessStatus.COMPLETE : CompletenessStatus.INCOMPLETE;
    OperationStatus newStatus = op.getStatus();
    if (op.getStatus() != OperationStatus.ACCEPTED) {
      if (allProvided) {
        newStatus = OperationStatus.READY;
      } else {
        newStatus = OperationStatus.WAITING_CLIENT;
      }
    }
    op.setCompletenessStatus(newCompleteness);
    op.setStatus(newStatus);
    operationRepository.save(op);
  }
}
