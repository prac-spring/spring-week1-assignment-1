package com.codesoom.assignment.parser;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.stream.Collectors;

public class TasksRequestParser {

    final private HttpExchange exchange;
    final private URI requestURI;
    final String path;

    public TasksRequestParser(HttpExchange exchange) {
        this.exchange = exchange;
        this.requestURI = exchange.getRequestURI();
        this.path = this.requestURI.getPath();
    }

    public long parseIdInPath() {
        return Long.parseLong(path.substring(path.length() - 1));
    }

    // TODO: requestBody가 비었을 때 동작 확인할 것
    public String parseRequestBody() {
        InputStream inputStream = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
