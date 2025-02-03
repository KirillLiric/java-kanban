package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import task.Epic;

import java.io.IOException;
import java.net.URI;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента.");
        super.handle(exchange);
    }

    @Override
    public void processGet(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.toString();
        String[] arrayPath = path.split("/");
        String response;
        if (arrayPath.length == 2) {
            response = gson.toJson(manager.getEpicMap());
            sendText(exchange, response, 200);
        } else if (arrayPath.length == 3) {
            String value = arrayPath[2];
            try {
                response = gson.toJson(manager.getEpicFromMap(Integer.parseInt(value)));
                sendText(exchange, response, 200);
            } catch (Exception e) {
                super.sendNotFound(exchange, value);
            }
        } else if ((arrayPath.length == 4) && (arrayPath[3].equals("subtasks"))) {
            String value = arrayPath[2];
            try {
                Epic epic = (Epic) manager.getEpicFromMap(Integer.parseInt(value));
                response = gson.toJson(epic.getEpicSubtaskMap());
                sendText(exchange, response, 200);
            } catch (Exception e) {
                sendNotFound(exchange, value);
            }
        }
    }

    @Override
    public void processPost(HttpExchange exchange) throws IOException {
        System.out.println("/POST");
        String requestBody = super.readRequestBody(exchange);
        Epic epic = gson.fromJson(requestBody, Epic.class);
        manager.addEpic(epic);
        sendText(exchange, "", 201);
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

