package com.accounting.platform.task;

import com.accounting.platform.task.dto.CreateTaskRequest;
import com.accounting.platform.task.dto.TaskResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
    TaskResponse response = taskService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<TaskResponse>> list() {
    return ResponseEntity.ok(taskService.list());
  }
}
