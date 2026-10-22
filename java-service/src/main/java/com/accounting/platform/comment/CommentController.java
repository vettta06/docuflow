package com.accounting.platform.comment;

import com.accounting.platform.comment.dto.CommentResponse;
import com.accounting.platform.comment.dto.CreateCommentRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentResponse> create(@Valid @RequestBody CreateCommentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(request));
  }

  @GetMapping("/operation/{operationId}")
  public ResponseEntity<List<CommentResponse>> getByOperation(@PathVariable Long operationId) {
    return ResponseEntity.ok(commentService.listByOperation(operationId));
  }

  @GetMapping("/document/{documentId}")
  public ResponseEntity<List<CommentResponse>> getByDocument(@PathVariable Long documentId) {
    return ResponseEntity.ok(commentService.listByDocument(documentId));
  }
}
