package handlers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {

    protected Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime .class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration .class, new DurationAdapter())
            .create();

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }
        return requestBodyBuilder.toString();
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String text = "method not allowed";
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(405, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    protected void writeToUser(HttpExchange exchange, String text) throws IOException {
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    protected void sendNotFound(HttpExchange exchange, String objectName) throws IOException {
        String message = String.format("{Ошибка: %s не найдена.}", objectName);
        sendText(exchange, message, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange, String objectName) throws IOException {
        String message = String.format("{Ошибка: %s пересекается с существующей задачей.}", objectName);
        sendText(exchange, message, 406);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                processGet(exchange);
                break;
            case "POST":
                processPost(exchange);
                break;
            case "DELETE":
                processDelete(exchange);
                break;
            default:
                writeToUser(exchange, "Данный метод не предусмотрен");
        }
    }

    public void processGet(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    public void processPost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    public void processDelete(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }
}
