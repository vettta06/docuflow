package com.accounting.platform.task;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.common.NotFoundException;
import com.accounting.platform.entity.Operation;
import com.accounting.platform.entity.Task;
import com.accounting.platform.entity.TaskStatus;
import com.accounting.platform.entity.User;
import com.accounting.platform.entity.UserRole;
import com.accounting.platform.repository.OperationRepository;
import com.accounting.platform.repository.TaskRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.CustomUserDetails;
import com.accounting.platform.security.SecurityUtils;
import com.accounting.platform.task.dto.CreateTaskRequest;
import com.accounting.platform.task.dto.TaskResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;
  private final OperationRepository operationRepository;
  private final UserRepository userRepository;

  @Transactional
  public TaskResponse create(CreateTaskRequest request) {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    if (current.getRole() == UserRole.CLIENT) {
      throw new InvalidOperationException("Клиент не может создавать задачи");
    }

    Operation operation =
        operationRepository
            .findById(request.operationId())
            .orElseThrow(() -> new NotFoundException("Операция не найдена"));

    Task task = new Task();
    task.setOperation(operation);
    task.setCreatedBy(userRepository.findById(current.getId()).orElseThrow());
    task.setDocumentType(request.documentType());
    task.setComment(request.comment());
    task.setStatus(TaskStatus.OPEN);
    task.setCreatedAt(LocalDateTime.now());

    return TaskResponse.from(taskRepository.save(task));
  }

  @Transactional(readOnly = true)
  public List<TaskResponse> list() {
    CustomUserDetails current = SecurityUtils.requireCurrentUser();

    List<Task> tasks;
    switch (current.getRole()) {
      case MANAGER -> tasks = taskRepository.findAll();
      case ACCOUNTANT -> tasks = taskRepository.findByCreatedById(current.getId());
      case CLIENT -> {
        User user =
            userRepository
                .findById(current.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getOrganization() == null) {
          tasks = List.of();
        } else {
          tasks = taskRepository.findByOperationOrganizationId(user.getOrganization().getId());
        }
      }
      default -> tasks = List.of();
    }

    return tasks.stream().map(TaskResponse::from).toList();
  }
}
