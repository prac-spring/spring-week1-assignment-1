package com.codesoom.assignment.controllers;

import com.codesoom.assignment.enums.HttpMethod;
import com.codesoom.assignment.httpHandlers.responses.ResponseNoContent;
import com.codesoom.assignment.httpHandlers.responses.ResponseSuccess;
import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.parser.TasksRequestParser;
import com.codesoom.assignment.repository.TaskRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.regex.Pattern;

public class TaskController implements HttpController{

    private final Pattern pathPattern;
    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.pathPattern = Pattern.compile("^/tasks/[0-9]*$");
        this.taskRepository = taskRepository;
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        TasksRequestParser parser = new TasksRequestParser(exchange);
        String content = "";
        long id = parser.parseIdInPath();
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        switch (method) {
            case GET:
                content = taskRepository.getTask(id).toJson();
                new ResponseSuccess(exchange).send(content);
                break;
            case PUT:
            case PATCH:
                String requestBody = parser.parseRequestBody();
                content = taskRepository.updateTask(id, Task.toObject(requestBody)).toJson();
                new ResponseSuccess(exchange).send(content);
                break;
            case DELETE:
                taskRepository.removeTask(id);
                new ResponseNoContent(exchange).send("");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isMatchedWith(String path) {
        return pathPattern.matcher(path).matches();
    }
}
