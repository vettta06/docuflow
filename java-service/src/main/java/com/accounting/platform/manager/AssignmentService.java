package com.accounting.platform.manager;

import com.accounting.platform.common.AlreadyExistsException;
import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.entity.AccountantClient;
import com.accounting.platform.entity.Organization;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.manager.dto.AssignRequest;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.OrganizationRepository;
import com.accounting.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountantClientRepository accountantClientRepository;

    @Transactional
    public void assign(AssignRequest request) {
        User accountant = userRepository.findById(request.accountantId())
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id " + request.accountantId() + " не найден"));

        if (accountant.getRole() != UserRole.ACCOUNTANT) {
            throw new InvalidOperationException(
                    "Пользователь с id " + accountant.getId() + " не является бухгалтером");
        }

        Organization org = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new NotFoundException(
                        "Организация с id " + request.organizationId() + " не найдена"));

        boolean alreadyAssigned = accountantClientRepository
                .findByAccountantId(accountant.getId())
                .stream()
                .anyMatch(ac -> ac.getOrganization().getId().equals(org.getId()));

        if (alreadyAssigned) {
            throw new AlreadyExistsException(
                    "Бухгалтер уже назначен на организацию " + org.getName());
        }

        AccountantClient ac = new AccountantClient();
        ac.setAccountant(accountant);
        ac.setOrganization(org);
        ac.setAssignedAt(LocalDateTime.now());

        accountantClientRepository.save(ac);
    }
}
