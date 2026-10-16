package com.accounting.platform.document;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.document.dto.DocumentResponse;
import com.accounting.platform.entity.AccountantClient;
import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.DocumentStatus;
import com.accounting.platform.entity.DocumentType;
import com.accounting.platform.entity.Operation;
import com.accounting.platform.entity.OperationDocument;
import com.accounting.platform.entity.User;
import com.accounting.platform.repository.AccountantClientRepository;
import com.accounting.platform.repository.DocumentRepository;
import com.accounting.platform.repository.OperationDocumentRepository;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import com.accounting.platform.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OperationRepository operationRepository;
    private final OperationDocumentRepository operationDocumentRepository;
    private final UserRepository userRepository;
    private final AccountantClientRepository accountantClientRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public DocumentResponse upload(MultipartFile file, Long operationId, String documentTypeStr) {
        CustomUserDetails current = SecurityUtils.requireCurrentUser();

        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new NotFoundException(
                        "Операция с id " + operationId + " не найдена"));

        checkAccessToOperation(operation, current);

        DocumentType documentType = parseDocumentType(documentTypeStr);

        String storedName = null;
        try {
            storedName = fileStorageService.store(file);

            Document doc = new Document();
            doc.setOrganization(operation.getOrganization());
            doc.setUploadedBy(userRepository.getReferenceById(current.getId()));
            doc.setDocumentType(documentType);
            doc.setStatus(DocumentStatus.UPLOADED);
            doc.setFilePath(storedName);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileSize(file.getSize());
            doc.setMimeType(file.getContentType());
            doc.setCreatedAt(LocalDateTime.now());
            doc.setUpdatedAt(LocalDateTime.now());

            Document saved = documentRepository.save(doc);

            OperationDocument link = new OperationDocument();
            link.setOperation(operation);
            link.setDocument(saved);
            link.setAddedAt(LocalDateTime.now());
            operationDocumentRepository.save(link);

            return DocumentResponse.from(saved, operation.getId());

        } catch (RuntimeException ex) {
            if (storedName != null) {
                fileStorageService.delete(storedName);
            }
            throw ex;
        }
    }

    private DocumentType parseDocumentType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidOperationException("Тип документа обязателен");
        }
        try {
            return DocumentType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidOperationException("Недопустимый тип документа: " + raw);
        }
    }

    private void checkAccessToOperation(Operation op, CustomUserDetails current) {
        Long orgId = op.getOrganization().getId();

        switch (current.getRole()) {
            case CLIENT -> {
                User user = userRepository.findById(current.getId())
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
                if (user.getOrganization() == null
                        || !user.getOrganization().getId().equals(orgId)) {
                    throw new NotFoundException("Операция с id " + op.getId() + " не найдена");
                }
            }
            case ACCOUNTANT -> {
                List<AccountantClient> assignments =
                        accountantClientRepository.findByAccountantId(current.getId());
                boolean assigned = assignments.stream()
                        .anyMatch(ac -> ac.getOrganization().getId().equals(orgId));
                if (!assigned) {
                    throw new NotFoundException("Операция с id " + op.getId() + " не найдена");
                }
            }
            case MANAGER -> {
            }
        }
    }
}
