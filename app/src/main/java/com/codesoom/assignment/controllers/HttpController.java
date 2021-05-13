package com.codesoom.assignment.controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface HttpController {
    void handleRequest(HttpExchange exchange) throws IOException;

    boolean isMatchedWith(String path);
}
