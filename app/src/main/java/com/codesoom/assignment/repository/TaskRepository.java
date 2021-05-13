package com.codesoom.assignment.repository;

import com.codesoom.assignment.httpHandlers.exceptions.TaskNotFoundException;
import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    final private List<Task> tasks = new ArrayList<>();
    private long taskId = 0;

    public List<Task> getAllTasks() {
        return tasks;
    }

    public Task getTask(long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException());
    }

    public void removeTask(long id) {
        Task task = getTask(id);
        tasks.remove(task);
    }

    public Task addTask(String requestBody) throws JsonProcessingException {
        if (requestBody.isBlank()) {
            throw new IllegalArgumentException("requestBody가 비었습니다.");
        }

        Task task = generateTask(requestBody);
        tasks.add(task);
        return task;
    }

    private Task generateTask(String requestBody) throws JsonProcessingException {
        Task task = Task.toObject(requestBody);
        task.setId(generateTaskId());
        return task;
    }

    private Long generateTaskId() {
        taskId += 1;
        return taskId;
    }

    public Task updateTask(long id, Task toTask) {
        Task task = getTask(id);
        task.setTitle(toTask.getTitle());
        return task;
    }
}
