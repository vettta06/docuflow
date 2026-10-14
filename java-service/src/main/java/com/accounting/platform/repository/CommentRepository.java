package com.accounting.platform.repository;

import com.accounting.platform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDocumentId(Long documentId);
    List<Comment> findByOperationId(Long operationId);
}
