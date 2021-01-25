// TODO
// 1. Read collection
// 2. Read item/element
// 3. Create
// 4. Update
// 5. Delete

package com.codesoom.assignment;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DemoHttpHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<Task> tasks = new ArrayList<>();


    public DemoHttpHandler() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("First");

        tasks.add(task);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // REST - CRUD
        // 1. Method - GET, POST, PUT/PATCH, DELETE...
        // 2. Path - "/", "/tasks", "/tasks/1"
        // 3. Headers, Body(Content)

        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        InputStream inputStream = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println(method + " " + path);
        if (!body.isEmpty()) {
            Task task = toTask(body);
            tasks.add(task);
        }

        String content = "Hello, World!";

        if (method.equals("GET") && path.equals("/tasks")) {
            content = tasksToJSON();
        }

        if (method.equals("POST") && path.equals("/tasks")) {
            content = "Create a new task.";
        }

        exchange.sendResponseHeaders(200, content.getBytes().length);

        OutputStream outputStream = exchange.getResponseBody();

        outputStream.write(content.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private Task toTask(String content) throws JsonProcessingException {
        return objectMapper.readValue(content, Task.class);
    }

    private String tasksToJSON() throws IOException {

        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            objectMapper.writeValue(outputStream, tasks);

            return outputStream.toString();
        }
    }
}