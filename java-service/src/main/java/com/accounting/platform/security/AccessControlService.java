package com.accounting.platform.security;

import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.entity.User;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {

  private final UserRepository userRepository;
  private final AccountantClientRepository accountantClientRepository;

  public void checkAccessToOrganization(Long organizationId) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    switch (current.getRole()) {
      case CLIENT -> {
        User user =
            userRepository
                .findById(current.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getOrganization() == null
            || !user.getOrganization().getId().equals(organizationId)) {
          throw new NotFoundException("Доступ запрещен");
        }
      }
      case ACCOUNTANT -> {
        boolean assigned =
            accountantClientRepository.findByAccountantId(current.getId()).stream()
                .anyMatch(ac -> ac.getOrganization().getId().equals(organizationId));
        if (!assigned) {
          throw new NotFoundException("Доступ запрещен");
        }
      }
      case MANAGER -> {}
    }
  }
}
