package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import task.Task;

import java.io.IOException;
import java.net.URI;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager manager;

    public TasksHandler(TaskManager manager){
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");
        super.handle(exchange);
    }

    @Override
    public void processGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.toString();
        String[] arrayPath = path.split("/");
        String response;
        if (arrayPath.length == 2) {
            response = gson.toJson(manager.getTaskMap());
            sendText(exchange, response, 200);
        } else if (arrayPath.length == 3) {
            String value = arrayPath[2];
            try {
                response = gson.toJson(manager.getTaskFromMap(Integer.parseInt(value)));
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
        Task task = gson.fromJson(requestBody, Task.class);
        int taskId;
        try {
            taskId = task.getId();
        } catch (Exception e) {
            taskId = -1;
        }
        if (taskId == -1) {
            try {
                manager.addTask(task);
                sendText(exchange, "", 201);
            } catch (RuntimeException e) {
                sendHasInteractions(exchange, task.getName());
            }
        } else {
            try {
                manager.updateTask(task);
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
        manager.deleteEpic(Integer.parseInt(value));
        sendText(exchange, "", 200);
    }
}
