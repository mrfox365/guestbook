package com.example.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;

public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void handle(HttpServletResponse resp, Exception e) throws IOException {
        int code = 500;
        String message = "Internal Server Error";

        if (e instanceof IllegalArgumentException) {
            code = 400;
            message = e.getMessage();
            log.warn("Validation error: {}", message);
        } else if (e instanceof IllegalStateException) {
            code = 409; // Conflict (бізнес-правило порушено)
            message = e.getMessage();
            log.warn("Business rule violation: {}", message);
        } else if (e.getMessage().contains("not found")) {
            code = 404;
            message = e.getMessage();
            log.warn("Not found: {}", message);
        } else {
            log.error("System error", e);
        }

        resp.setStatus(code);
        resp.setContentType("application/json");
        mapper.writeValue(resp.getOutputStream(), Map.of("error", message));
    }
}