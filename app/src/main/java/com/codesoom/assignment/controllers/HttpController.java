package com.codesoom.assignment.controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface HttpController {
    void handleRequest() throws IOException;
}
