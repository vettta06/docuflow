package com.accounting.platform.operation;

import com.accounting.platform.operation.dto.CreateOperationRequest;
import com.accounting.platform.operation.dto.OperationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @PostMapping
    public ResponseEntity<OperationResponse> create(
            @Valid @RequestBody CreateOperationRequest request
    ) {
        OperationResponse response = operationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OperationResponse>> list() {
        return ResponseEntity.ok(operationService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.getById(id));
    }
}
