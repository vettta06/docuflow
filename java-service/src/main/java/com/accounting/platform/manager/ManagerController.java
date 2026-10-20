package com.accounting.platform.manager;

import com.accounting.platform.manager.dto.AccountantResponse;
import com.accounting.platform.manager.dto.AssignRequest;
import com.accounting.platform.manager.dto.ClientResponse;
import com.accounting.platform.manager.dto.CreateAccountantRequest;
import com.accounting.platform.manager.dto.CreateClientRequest;
import com.accounting.platform.manager.dto.CreateOrganizationRequest;
import com.accounting.platform.manager.dto.OrganizationResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

  private final OrganizationService organizationService;
  private final AccountantService accountantService;
  private final ClientService clientService;
  private final AssignmentService assignmentService;

  @PostMapping("/organizations")
  public ResponseEntity<OrganizationResponse> createOrganization(
      @Valid @RequestBody CreateOrganizationRequest request) {
    OrganizationResponse response = organizationService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/organizations")
  public ResponseEntity<List<OrganizationResponse>> listOrganizations() {
    return ResponseEntity.ok(organizationService.listAll());
  }

  @PostMapping("/accountants")
  public ResponseEntity<AccountantResponse> createAccountant(
      @Valid @RequestBody CreateAccountantRequest request) {
    AccountantResponse response = accountantService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/accountants")
  public ResponseEntity<List<AccountantResponse>> listAccountants() {
    return ResponseEntity.ok(accountantService.listAll());
  }

  @PostMapping("/clients")
  public ResponseEntity<ClientResponse> createClient(
      @Valid @RequestBody CreateClientRequest request) {
    ClientResponse response = clientService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/assignments")
  public ResponseEntity<Map<String, String>> assign(@Valid @RequestBody AssignRequest request) {
    assignmentService.assign(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("message", "Бухгалтер успешно назначен на организацию"));
  }
}
