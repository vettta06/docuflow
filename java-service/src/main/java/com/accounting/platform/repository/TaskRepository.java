package com.accounting.platform.repository;

import com.accounting.platform.entity.Task;
import com.accounting.platform.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedToId(Long userId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByOperationId(Long operationId);
}
