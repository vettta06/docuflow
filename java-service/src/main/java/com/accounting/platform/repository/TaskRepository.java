package com.accounting.platform.repository;

import com.accounting.platform.entity.Task;
import com.accounting.platform.entity.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByAssignedToId(Long userId);

  List<Task> findByStatus(TaskStatus status);

  List<Task> findByOperationId(Long operationId);
}
