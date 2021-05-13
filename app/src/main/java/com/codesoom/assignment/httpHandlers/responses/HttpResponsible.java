package com.codesoom.assignment.httpHandlers.responses;

import java.io.IOException;

public interface HttpResponsible {
    void send(String content) throws IOException;
}
