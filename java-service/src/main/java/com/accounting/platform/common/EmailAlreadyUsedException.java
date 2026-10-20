package com.accounting.platform.common;

public class EmailAlreadyUsedException extends RuntimeException {

  public EmailAlreadyUsedException(String email) {
    super("Пользователь с email " + email + " уже зарегистрирован");
  }
}
