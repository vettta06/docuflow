package com.accounting.platform.manager;

import com.accounting.platform.common.AlreadyExistsException;
import com.accounting.platform.entity.Organization;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.manager.dto.ClientResponse;
import com.accounting.platform.manager.dto.CreateClientRequest;
import com.accounting.platform.repository.OrganizationRepository;
import com.accounting.platform.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final UserRepository userRepository;
  private final OrganizationRepository organizationRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public ClientResponse create(CreateClientRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new AlreadyExistsException(
          "Пользователь с email " + request.email() + " уже существует");
    }
    if (organizationRepository.findByInn(request.organizationInn()).isPresent()) {
      throw new AlreadyExistsException(
          "Организация с ИНН " + request.organizationInn() + " уже существует");
    }

    Organization org = new Organization();
    org.setName(request.organizationName());
    org.setInn(request.organizationInn());
    org.setOgrn(request.organizationOgrn());
    org.setAddress(request.organizationAddress());
    org.setCreatedAt(LocalDateTime.now());
    Organization savedOrg = organizationRepository.save(org);

    User client = new User();
    client.setRole(UserRole.CLIENT);
    client.setEmail(request.email());
    client.setFullName(request.fullName());
    client.setPhone(request.phone());
    client.setPasswordHash(passwordEncoder.encode(request.password()));
    client.setOrganization(savedOrg);
    client.setCreatedAt(LocalDateTime.now());
    User savedClient = userRepository.save(client);

    return ClientResponse.from(savedClient);
  }
}
