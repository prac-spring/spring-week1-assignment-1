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

public class TasksController implements HttpController {

    private final HttpExchange exchange;
    private TaskRepository taskRepository;
    private TasksRequestParser parser;
    private ObjectMapper objectMapper;

    public TasksController(HttpExchange exchange, TaskRepository taskRepository) {
        this.exchange = exchange;
        this.taskRepository = taskRepository;
        this.parser = new TasksRequestParser(exchange);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handleRequest() throws IOException {
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

    public String toJson(Object object) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, object);

        return outputStream.toString();
    }
}
