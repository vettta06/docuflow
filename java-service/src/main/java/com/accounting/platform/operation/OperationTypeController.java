package com.accounting.platform.operation;

import com.accounting.platform.operation.dto.OperationTypeResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operation-types")
@RequiredArgsConstructor
public class OperationTypeController {

  private final OperationTypeService operationTypeService;

  @GetMapping
  public ResponseEntity<List<OperationTypeResponse>> listAll() {
    return ResponseEntity.ok(operationTypeService.listAll());
  }
}
