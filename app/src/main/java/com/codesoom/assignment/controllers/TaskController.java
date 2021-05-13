package com.codesoom.assignment.controllers;

import com.codesoom.assignment.enums.HttpMethod;
import com.codesoom.assignment.enums.HttpStatus;
import com.codesoom.assignment.httpHandlers.datas.HttpResponseData;
import com.codesoom.assignment.httpHandlers.responses.ResponseNoContent;
import com.codesoom.assignment.httpHandlers.responses.ResponseSuccess;
import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.parser.TasksRequestParser;
import com.codesoom.assignment.repository.TaskRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class TaskController implements HttpController{

    private final HttpExchange exchange;
    private final TaskRepository taskRepository;
    private final TasksRequestParser parser;

    public TaskController(HttpExchange exchange, TaskRepository taskRepository) {
        this.exchange = exchange;
        this.taskRepository = taskRepository;
        this.parser = new TasksRequestParser(exchange);
    }

    @Override
    public void handleRequest() throws IOException {
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
}
