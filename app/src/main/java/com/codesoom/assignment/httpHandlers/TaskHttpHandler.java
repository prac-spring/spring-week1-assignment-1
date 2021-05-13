package com.codesoom.assignment.httpHandlers;

import com.codesoom.assignment.controllers.TaskController;
import com.codesoom.assignment.controllers.TasksController;
import com.codesoom.assignment.enums.HttpStatus;
import com.codesoom.assignment.httpHandlers.exceptions.TaskNotFoundException;
import com.codesoom.assignment.httpHandlers.responses.ResponseBadRequest;
import com.codesoom.assignment.httpHandlers.responses.ResponseConflict;
import com.codesoom.assignment.httpHandlers.responses.ResponseNotFound;
import com.codesoom.assignment.repository.TaskRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;

public class TaskHttpHandler implements HttpHandler {

    private TaskRepository taskRepository = new TaskRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        try {
            if (path.equals("/tasks")) {
                TasksController tasksController = new TasksController(exchange, taskRepository);
                tasksController.handleRequest();
            } else if (path.startsWith("/tasks/")) {
                TaskController taskController = new TaskController(exchange, taskRepository);
                taskController.handleRequest();
            }
        } catch (TaskNotFoundException e) {
            new ResponseNotFound(exchange).send(HttpStatus.NOT_FOUND.name());
        } catch (IllegalArgumentException e) {
            new ResponseBadRequest(exchange).send(HttpStatus.BAD_REQUEST.name());
        } catch (Exception e) {
            new ResponseConflict(exchange).send(HttpStatus.CONFLICT.name());
        }
    }
}
