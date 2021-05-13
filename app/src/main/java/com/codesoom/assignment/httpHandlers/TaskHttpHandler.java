package com.codesoom.assignment.httpHandlers;

import com.codesoom.assignment.controllers.TaskController;
import com.codesoom.assignment.controllers.TasksController;
import com.codesoom.assignment.enums.HttpStatus;
import com.codesoom.assignment.httpHandlers.exceptions.TaskNotFoundException;
import com.codesoom.assignment.httpHandlers.responses.ResponseBadRequest;
import com.codesoom.assignment.httpHandlers.responses.ResponseConflict;
import com.codesoom.assignment.httpHandlers.responses.ResponseNotFound;
import com.codesoom.assignment.repository.TaskRepository;
import com.codesoom.assignment.routers.AppRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class TaskHttpHandler implements HttpHandler {

    private TaskRepository taskRepository = new TaskRepository();
    private AppRouter appRouter = new AppRouter();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        TaskController taskController = new TaskController(taskRepository);
        TasksController tasksController = new TasksController(taskRepository, objectMapper);
        appRouter.register(taskController);
        appRouter.register(tasksController);

        try {
            appRouter.handleRequest(exchange);
        } catch (TaskNotFoundException e) {
            new ResponseNotFound(exchange).send(HttpStatus.NOT_FOUND.name());
        } catch (IllegalArgumentException e) {
            new ResponseBadRequest(exchange).send(HttpStatus.BAD_REQUEST.name());
        } catch (Exception e) {
            new ResponseConflict(exchange).send(HttpStatus.CONFLICT.name());
        }
    }
}
