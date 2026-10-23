package com.accounting.platform.dashboard;

import com.accounting.platform.dashboard.dto.AccountantLoad;
import com.accounting.platform.dashboard.dto.ManagerDashboardResponse;
import com.accounting.platform.entity.DocumentStatus;
import com.accounting.platform.entity.OperationStatus;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.DocumentRepository;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.OrganizationRepository;
import com.accounting.platform.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerDashboardService {

  private final UserRepository userRepository;
  private final AccountantClientRepository accountantClientRepository;
  private final OperationRepository operationRepository;
  private final DocumentRepository documentRepository;
  private final OrganizationRepository organizationRepository;

  @Transactional(readOnly = true)
  public ManagerDashboardResponse getDashboard() {
    List<User> accountants = userRepository.findByRole(UserRole.ACCOUNTANT);

    List<AccountantLoad> loads =
        accountants.stream()
            .map(
                accountant -> {
                  List<Long> orgIds =
                      accountantClientRepository.findByAccountantId(accountant.getId()).stream()
                          .map(ac -> ac.getOrganization().getId())
                          .toList();

                  long activeOps =
                      operationRepository.findAll().stream()
                          .filter(
                              op ->
                                  op.getAccountant() != null
                                      && op.getAccountant().getId().equals(accountant.getId()))
                          .filter(
                              op ->
                                  op.getStatus() == OperationStatus.READY
                                      || op.getStatus() == OperationStatus.IN_PROGRESS)
                          .count();

                  long waiting =
                      operationRepository.findAll().stream()
                          .filter(
                              op ->
                                  op.getAccountant() != null
                                      && op.getAccountant().getId().equals(accountant.getId()))
                          .filter(op -> op.getStatus() == OperationStatus.WAITING_CLIENT)
                          .count();

                  long confirmedDocs =
                      documentRepository.findAll().stream()
                          .filter(doc -> orgIds.contains(doc.getOrganization().getId()))
                          .filter(doc -> doc.getStatus() == DocumentStatus.CONFIRMED)
                          .count();

                  return new AccountantLoad(
                      accountant.getId(),
                      accountant.getFullName(),
                      activeOps,
                      waiting,
                      confirmedDocs);
                })
            .toList();

    long totalOrgs = organizationRepository.count();
    long totalDocs = documentRepository.count();

    return new ManagerDashboardResponse(loads, totalOrgs, totalDocs);
  }
}
