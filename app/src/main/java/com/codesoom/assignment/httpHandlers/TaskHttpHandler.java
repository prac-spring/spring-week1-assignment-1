package com.codesoom.assignment.httpHandlers;

import com.codesoom.assignment.enums.HttpMethod;
import com.codesoom.assignment.enums.HttpStatus;
import com.codesoom.assignment.httpHandlers.datas.HttpResponseData;
import com.codesoom.assignment.httpHandlers.exceptions.TaskNotFoundException;
import com.codesoom.assignment.httpHandlers.responses.ResponseBadRequest;
import com.codesoom.assignment.httpHandlers.responses.ResponseConflict;
import com.codesoom.assignment.httpHandlers.responses.ResponseNotFound;
import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.parser.TasksRequestParser;
import com.codesoom.assignment.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class TaskHttpHandler implements HttpHandler {

    private TaskRepository taskRepository = new TaskRepository();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        try {
            HttpResponseData responseData = new HttpResponseData(HttpStatus.OK, "초기화 값");

            if (path.equals("/tasks")) {
                responseData = processTasks(exchange);
            } else if (path.startsWith("/tasks/")) {
                responseData = processTask(exchange);
            }

            sendResponse(exchange, responseData.getStatus(), responseData.getContent());
        } catch (TaskNotFoundException e) {
            new ResponseNotFound(exchange).send(HttpStatus.NOT_FOUND.name());
        } catch (IllegalArgumentException e) {
            new ResponseBadRequest(exchange).send(HttpStatus.BAD_REQUEST.name());
        } catch (Exception e) {
            new ResponseConflict(exchange).send(HttpStatus.CONFLICT.name());
        }
    }

    private HttpResponseData processTask(HttpExchange exchange) throws IOException {
        TasksRequestParser parser = new TasksRequestParser(exchange);
        HttpStatus status = HttpStatus.OK;
        String content = "";
        long id = parser.parseIdInPath();
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        switch (method) {
            case GET:
                content = taskRepository.getTask(id).toJson();
                break;
            case PUT:
            case PATCH:
                String requestBody = parser.parseRequestBody();
                content = taskRepository.updateTask(id, Task.toObject(requestBody)).toJson();
                break;
            case DELETE:
                taskRepository.removeTask(id);
                status = HttpStatus.NO_CONTENT;
                break;
            default:
                break;
        }
        return new HttpResponseData(status, content);
    }

    private HttpResponseData processTasks(HttpExchange exchange) throws IOException {
        TasksRequestParser parser = new TasksRequestParser(exchange);
        HttpStatus status = HttpStatus.OK;
        String content = "";

        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        switch (method) {
            case GET:
                content = toJson(taskRepository.getAllTasks());
                break;
            case POST:
                Task newTask = taskRepository.addTask(parser.parseRequestBody());
                status = HttpStatus.CREATED;
                content = newTask.toJson();
                break;
            default:
                break;
        }
        return new HttpResponseData(status, content);
    }

    private void sendResponse(HttpExchange exchange, HttpStatus status, String content) throws IOException {
        // "WARNING: sendResponseHeaders: rCode = 204: forcing contentLen = -1" 경고 제거 위해, 204 상태코드일 때의 분기 추가
        long contentLength = status == HttpStatus.NO_CONTENT ? -1 : content.getBytes().length;
        exchange.sendResponseHeaders(status.getCode(), contentLength);
        sendResponseBody(exchange, content);
    }

    private void sendResponseBody(HttpExchange exchange, String content) throws IOException {
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.flush();
        responseBody.close();
    }

    public String toJson(Object object) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, object);

        return outputStream.toString();
    }
}
