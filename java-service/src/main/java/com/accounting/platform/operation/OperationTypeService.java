package com.accounting.platform.operation;

import com.accounting.platform.operation.dto.OperationTypeResponse;
import com.accounting.platform.repository.OperationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationTypeService {

    private final OperationTypeRepository operationTypeRepository;

    @Transactional(readOnly = true)
    public List<OperationTypeResponse> listAll() {
        return operationTypeRepository.findAll().stream()
                .map(OperationTypeResponse::from)
                .toList();
    }
}
