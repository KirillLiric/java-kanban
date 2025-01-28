import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.*;
import task.Epic;
import task.Subtask;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTaskServer {

    private static final Gson gson = new Gson();
    private final InMemoryTaskManager inMemoryTaskManager;
    private final FileBackedTaskManager fileBackedTaskManager;
    HttpServer server;

    HttpTaskServer(InMemoryTaskManager inMemoryTaskManager, FileBackedTaskManager fileBackedTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
        this.fileBackedTaskManager = fileBackedTaskManager;

        try {
            server = HttpServer.create(new InetSocketAddress(8800), 0);
            server.createContext("/tasks", new TasksHandler());
            server.createContext("/subtasks", new SubtasksHandler());
            server.createContext("/epics", new EpicsHandler());
            server.createContext("/history", new HistoryHandler());
            server.createContext("/prioritized", new PrioritizedHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Обработчик для /tasks
    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            int option;
            try {
                String stringOption = path.split("/")[2];
                option = Integer.parseInt(stringOption);
            } catch (ArrayIndexOutOfBoundsException e) {
                option = -1;
            }
            String response;
            switch (method) {
                case "GET":
                    if(option == -1) {
                        response = gson.toJson(inMemoryTaskManager.getTaskMap());
                    } else {
                        response = gson.toJson(inMemoryTaskManager.getTaskFromMap(option));
                    }
                    break;
                case "POST":
                    String stringTask = exchange.getRequestBody().toString();
                    if(option == -1) {
                        inMemoryTaskManager.addTask(FileBackedTaskManager.fromString(stringTask));
                    } else {
                        inMemoryTaskManager.updateTask(FileBackedTaskManager.fromString(stringTask));
                    }
                    break;
                case "DELETE":
                    inMemoryTaskManager.deleteTask(option);
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /subtasks
    class SubtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            int option;
            try {
                String stringOption = path.split("/")[2];
                option = Integer.parseInt(stringOption);
            } catch (ArrayIndexOutOfBoundsException e) {
                option = -1;
            }
            String response;
            switch (method) {
                case "GET":
                    if(option == -1) {
                        response = gson.toJson(inMemoryTaskManager.getSubtaskMap());
                    } else {
                        response = gson.toJson(inMemoryTaskManager.getSubtaskFromMap(option));
                    }
                    break;
                case "POST":
                    String stringTask = exchange.getRequestBody().toString();
                    if(option == -1) {
                        inMemoryTaskManager.addSubtask((Subtask) FileBackedTaskManager.fromString(stringTask));
                    } else {
                        inMemoryTaskManager.updateSubtask((Subtask) FileBackedTaskManager.fromString(stringTask));
                    }
                    break;
                case "DELETE":
                    inMemoryTaskManager.deleteSubtask(option);
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /epics
    class EpicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            int option;
            try {
                String stringOption = path.split("/")[2];
                option = Integer.parseInt(stringOption);
            } catch (ArrayIndexOutOfBoundsException e) {
                option = -1;
            }
            String response;
            switch (method) {
                case "GET":
                    if(option == -1) {
                        response = gson.toJson(inMemoryTaskManager.getEpicMap());
                    } else {
                        response = gson.toJson(inMemoryTaskManager.getEpicFromMap(option));
                    }
                    break;
                case "POST":
                    String stringTask = exchange.getRequestBody().toString();
                    if(option == -1) {
                        inMemoryTaskManager.addEpic((Epic) FileBackedTaskManager.fromString(stringTask));
                    } else {
                        inMemoryTaskManager.updateEpic((Epic) FileBackedTaskManager.fromString(stringTask));
                    }
                    break;
                case "DELETE":
                    inMemoryTaskManager.deleteEpic(option);
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /history
    class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String method = exchange.getRequestMethod();

            String response;
            switch (method) {
                case "GET":
                    response = gson.toJson(fileBackedTaskManager.getHistory());
                    break;
                default:
                    response = "Такого метода нет";
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /prioritized
    class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String method = exchange.getRequestMethod();

            String response;
            switch (method) {
                case "GET":
                    response = gson.toJson(fileBackedTaskManager.getPrioritizedTasks());
                    break;
                default:
                    response = "Такого метода нет";
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

    public static void main(String[] args) throws IOException {

        File file = File.createTempFile("tempFile", ".txt");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        TaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(file.toPath());
        HttpTaskServer httpTaskServer = new HttpTaskServer((InMemoryTaskManager) inMemoryTaskManager,
                (FileBackedTaskManager) fileBackedTaskManager);
        httpTaskServer.server.start();


    }
}