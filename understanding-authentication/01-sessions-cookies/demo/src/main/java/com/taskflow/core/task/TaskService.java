package com.taskflow.core.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository tasks;

    public List<Task> findAll() {
        return tasks.findAll();
    }

    public Task create(String title, String username) {
        Task task = Task.builder()
                .title(title)
                .createdBy(username)
                .build();
        return tasks.save(task);
    }
}
