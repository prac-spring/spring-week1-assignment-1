package com.codesoom.assignment.httpHandlers.responses;

import com.codesoom.assignment.enums.HttpStatus;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseNoContent implements HttpResponsible {

    private HttpExchange exchange;
    private HttpStatus status;

    public ResponseNoContent(HttpExchange exchange) {
        this.status = HttpStatus.NO_CONTENT;
        this.exchange = exchange;
    }

    @Override
    public void send(String content) throws IOException {
        // "WARNING: sendResponseHeaders: rCode = 204: forcing contentLen = -1" 경고를
        // 제거하기 위해 contentLength를 -1로 설정했습니다.
        // 또한 responseBody을 채우지 않도록 코드 작성했습니다.
        long contentLength = -1;
        exchange.sendResponseHeaders(status.getCode(), contentLength);
        sendResponseBody("");
    }

    private void sendResponseBody(String content) throws IOException {
        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.flush();
        responseBody.close();
    }
}
