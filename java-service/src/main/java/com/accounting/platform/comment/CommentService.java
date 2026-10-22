package com.accounting.platform.comment;

import com.accounting.platform.comment.dto.CommentResponse;
import com.accounting.platform.comment.dto.CreateCommentRequest;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.entity.Comment;
import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.Operation;
import com.accounting.platform.repository.CommentRepository;
import com.accounting.platform.repository.DocumentRepository;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.AccessControlService;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final OperationRepository operationRepository;
  private final DocumentRepository documentRepository;
  private final UserRepository userRepository;
  private final AccessControlService accessControlService;

  @Transactional
  public CommentResponse create(CreateCommentRequest request) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    Comment comment = new Comment();
    comment.setAuthor(
        userRepository
            .findById(current.getId())
            .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    comment.setText(request.text());
    comment.setCreatedAt(LocalDateTime.now());

    if (request.operationId() != null) {
      Operation operation =
          operationRepository
              .findById(request.operationId())
              .orElseThrow(() -> new NotFoundException("Операция не найдена"));
      accessControlService.checkAccessToOrganization(operation.getOrganization().getId());
      comment.setOperation(operation);
    } else {
      Document document =
          documentRepository
              .findById(request.documentId())
              .orElseThrow(() -> new NotFoundException("Документ не найден"));
      accessControlService.checkAccessToOrganization(document.getOrganization().getId());
      comment.setDocument(document);
    }

    return CommentResponse.from(commentRepository.save(comment));
  }

  @Transactional(readOnly = true)
  public List<CommentResponse> listByOperation(Long operationId) {
    Operation operation =
        operationRepository
            .findById(operationId)
            .orElseThrow(() -> new NotFoundException("Операция не найдена"));

    accessControlService.checkAccessToOrganization(operation.getOrganization().getId());

    return commentRepository.findByOperationId(operationId).stream()
        .map(CommentResponse::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<CommentResponse> listByDocument(Long documentId) {
    Document document =
        documentRepository
            .findById(documentId)
            .orElseThrow(() -> new NotFoundException("Документ не найден"));

    accessControlService.checkAccessToOrganization(document.getOrganization().getId());

    return commentRepository.findByDocumentId(documentId).stream()
        .map(CommentResponse::from)
        .toList();
  }
}
