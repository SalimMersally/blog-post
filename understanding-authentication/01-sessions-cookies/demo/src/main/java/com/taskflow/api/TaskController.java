package com.taskflow.api;

import com.taskflow.core.task.Task;
import com.taskflow.core.task.TaskService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService tasks;

    @GetMapping
    public List<TaskResponse> list() {
        return tasks.findAll().stream().map(TaskResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@RequestBody NewTask body, Authentication authentication) {
        Task task = tasks.create(body.getTitle(), authentication.getName());
        return TaskResponse.from(task);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewTask {
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskResponse {
        private Long id;
        private String title;
        private String createdBy;
        private Instant createdAt;

        private static TaskResponse from(Task task) {
            return TaskResponse.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .createdBy(task.getCreatedBy())
                    .createdAt(task.getCreatedAt())
                    .build();
        }
    }
}
