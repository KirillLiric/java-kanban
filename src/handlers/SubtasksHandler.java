package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.net.URI;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента.");
        super.handle(exchange);
    }

    @Override
    public void processGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.toString();
        String[] arrayPath = path.split("/");
        String response;
        if (arrayPath.length == 2) {
            response = gson.toJson(manager.getSubtaskMap());
            sendText(exchange, response, 200);
        } else if (arrayPath.length == 3) {
            String value = arrayPath[2];
            try {
                response = gson.toJson(manager.getSubtaskFromMap(Integer.parseInt(value)));
                sendText(exchange, response, 200);
            } catch (Exception e) {
                super.sendNotFound(exchange, value);
            }
        }
    }

    @Override
    public void processPost(HttpExchange exchange) throws IOException {
        System.out.println("/POST");
        String requestBody = readRequestBody(exchange);
        Subtask task = gson.fromJson(requestBody, Subtask.class);
        int taskId;
        try {
            taskId = task.getId();
        } catch (Exception e) {
            taskId = -1;
        }
        if (taskId == -1) {
            try {
                manager.addSubtask(task);
                sendText(exchange, "", 201);
            } catch (RuntimeException e) {
                sendHasInteractions(exchange, task.getName());
            }
        } else {
            try {
                manager.updateSubtask(task);
                sendText(exchange, "", 201);
            } catch (RuntimeException e) {
                sendHasInteractions(exchange, task.getName());
            }
        }
    }

    @Override
    public void processDelete(HttpExchange exchange) throws IOException {
        System.out.println("/DELETE");
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.toString();
        String[] arrayPath = path.split("/");
        String value = arrayPath[2];
        manager.deleteSubtask(Integer.parseInt(value));
        sendText(exchange, "", 200);
    }
}