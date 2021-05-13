package com.codesoom.assignment.controllers;

import com.codesoom.assignment.enums.HttpMethod;
import com.codesoom.assignment.httpHandlers.responses.ResponseCreated;
import com.codesoom.assignment.httpHandlers.responses.ResponseSuccess;
import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.parser.TasksRequestParser;
import com.codesoom.assignment.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class TasksController implements HttpController {

    final private Pattern pathPattern;
    private TaskRepository taskRepository;
    private TasksRequestParser parser;
    private ObjectMapper objectMapper;

    public TasksController(TaskRepository taskRepository, ObjectMapper objectMapper) {
        this.pathPattern = Pattern.compile("^/tasks$");
        this.taskRepository = taskRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        TasksRequestParser parser = new TasksRequestParser(exchange);
        String content = "";
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        switch (method) {
            case GET:
                content = toJson(taskRepository.getAllTasks());
                new ResponseSuccess(exchange).send(content);
                break;
            case POST:
                Task newTask = taskRepository.addTask(parser.parseRequestBody());
                content = newTask.toJson();
                new ResponseCreated(exchange).send(content);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isMatchedWith(String path) {
        return pathPattern.matcher(path).matches();
    }

    public String toJson(Object object) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, object);

        return outputStream.toString();
    }
}
