package com.taskflow.infra.task;

import com.taskflow.core.task.Task;
import com.taskflow.core.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaTaskRepository implements TaskRepository {

    private final SpringDataTaskRepository tasks;

    @Override
    public Task save(Task task) {
        return toDomain(tasks.save(toEntity(task)));
    }

    @Override
    public List<Task> findAll() {
        return tasks.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
    }

    private TaskEntity toEntity(Task task) {
        return TaskEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .createdBy(task.getCreatedBy())
                .createdAt(task.getCreatedAt())
                .build();
    }

    private Task toDomain(TaskEntity entity) {
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
