package com.codesoom.assignment.routers;

import com.codesoom.assignment.controllers.HttpController;
import com.codesoom.assignment.httpHandlers.exceptions.TaskNotFoundException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class AppRouter {

    final private ArrayList<HttpController> controllers = new ArrayList<>();

    public void register(HttpController controller) {
        controllers.add(controller);
    }

    public void handleRequest(HttpExchange exchange) throws IOException {
        HttpController controller = findControllerMatchingPath(exchange);
        controller.handleRequest(exchange);
    }

    private HttpController findControllerMatchingPath(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();

        return controllers.stream()
                .filter(controller -> controller.isMatchedWith(path))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException());
    }
}
