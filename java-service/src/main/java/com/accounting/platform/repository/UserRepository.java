package com.accounting.platform.repository;

import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findByRole(UserRole role);
}
