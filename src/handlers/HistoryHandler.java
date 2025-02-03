package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import task.Epic;

import java.io.IOException;
import java.net.URI;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }

    @Override
    public void processGet(HttpExchange exchange) throws IOException {
        String response;
        response = gson.toJson(manager.getHistory());
        sendText(exchange, response, 200);
    }
}
