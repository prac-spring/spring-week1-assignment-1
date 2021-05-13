package com.codesoom.assignment.httpHandlers.responses;

import com.codesoom.assignment.enums.HttpStatus;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseBadRequest implements HttpResponsible {
    private HttpExchange exchange;
    private HttpStatus status;

    public ResponseBadRequest(HttpExchange exchange) {
        this.status = HttpStatus.BAD_REQUEST;
        this.exchange = exchange;
    }

    @Override
    public void send(String content) throws IOException {
        long contentLength = content.getBytes().length;
        exchange.sendResponseHeaders(status.getCode(), contentLength);
        sendResponseBody(content);
    }

    private void sendResponseBody(String content) throws IOException {
        OutputStream responseBody = this.exchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.flush();
        responseBody.close();
    }
}
