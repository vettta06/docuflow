package com.accounting.platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static CustomUserDetails getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }
    Object principal = auth.getPrincipal();
    if (principal instanceof CustomUserDetails custom) {
      return custom;
    }
    return null;
  }

  public static CustomUserDetails requireCurrentUser() {
    CustomUserDetails user = getCurrentUser();
    if (user == null) {
      throw new IllegalStateException("Текущий пользователь не аутентифицирован");
    }
    return user;
  }
}
