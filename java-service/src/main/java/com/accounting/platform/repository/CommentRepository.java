package com.accounting.platform.repository;

import com.accounting.platform.entity.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByDocumentId(Long documentId);

  List<Comment> findByOperationId(Long operationId);
}
