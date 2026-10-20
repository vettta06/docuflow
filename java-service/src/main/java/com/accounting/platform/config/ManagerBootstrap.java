package com.accounting.platform.config;

import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerBootstrap implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final BootstrapProperties bootstrapProperties;

  @Override
  public void run(String... args) {
    List<User> existingManagers = userRepository.findByRole(UserRole.MANAGER);

    if (!existingManagers.isEmpty()) {
      log.info("Bootstrap: найдено {} менеджеров, создание не требуется", existingManagers.size());
      return;
    }

    User manager = new User();
    manager.setRole(UserRole.MANAGER);
    manager.setEmail(bootstrapProperties.email());
    manager.setFullName(bootstrapProperties.fullName());
    manager.setPasswordHash(passwordEncoder.encode(bootstrapProperties.password()));
    manager.setCreatedAt(LocalDateTime.now());

    userRepository.save(manager);

    log.warn(
        "Bootstrap: создан первый MANAGER с email '{}'. Пароль: см. конфигурацию/переменные окружения.",
        bootstrapProperties.email());
  }
}
