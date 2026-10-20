package com.accounting.platform.manager;

import com.accounting.platform.common.AlreadyExistsException;
import com.accounting.platform.entity.Organization;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.manager.dto.AccountantResponse;
import com.accounting.platform.manager.dto.CreateAccountantRequest;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.OrganizationRepository;
import com.accounting.platform.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountantService {

  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final AccountantClientRepository accountantClientRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public AccountantResponse create(CreateAccountantRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new AlreadyExistsException(
          "Пользователь с email " + request.email() + " уже существует");
    }

    User accountant = new User();
    accountant.setRole(UserRole.ACCOUNTANT);
    accountant.setEmail(request.email());
    accountant.setFullName(request.fullName());
    accountant.setPhone(request.phone());
    accountant.setPasswordHash(passwordEncoder.encode(request.password()));
    accountant.setCreatedAt(LocalDateTime.now());

    User saved = userRepository.save(accountant);
    return AccountantResponse.from(saved, List.of());
  }

  @Transactional(readOnly = true)
  public List<AccountantResponse> listAll() {
    List<User> accountants = userRepository.findByRole(UserRole.ACCOUNTANT);

    return accountants.stream()
        .map(
            acc -> {
              List<Organization> clients =
                  accountantClientRepository.findByAccountantId(acc.getId()).stream()
                      .map(ac -> ac.getOrganization())
                      .toList();
              return AccountantResponse.from(acc, clients);
            })
        .toList();
  }
}
