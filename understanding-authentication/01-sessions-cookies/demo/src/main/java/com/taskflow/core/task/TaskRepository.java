package com.taskflow.core.task;

import java.util.List;

public interface TaskRepository {

    Task save(Task task);

    List<Task> findAll();
}
