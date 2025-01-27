import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTaskServer {

    private static final Gson gson = new Gson();
    private final TaskManager taskManager = new InMemoryTaskManager();

    HttpServer server;

    {
        try {
            server = HttpServer.create(new InetSocketAddress(8800), 0);
            server.createContext("/tasks", new TasksHandler());
            server.createContext("/subtasks", new SubtasksHandler());
            server.createContext("/epics", new EpicsHandler());
            server.createContext("/history", new HistoryHandler());
            server.createContext("/prioritized", new PrioritizedHandler();
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
            String addMethod;
            try {
                addMethod = path.split("/")[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                addMethod = null;
            }
            String response;
            switch (method) {
                case "GET":
                    if(addMethod == null) {
                        response = taskManager.getTaskMap();
                    } else {
                        response = ;
                    }
                    break;
                case
            }


            String response = "Tasks endpoint";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /subtasks
    static class SubtasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Subtasks endpoint";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /epics
    static class EpicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Epics endpoint";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /history
    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "History endpoint";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Обработчик для /prioritized
    static class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Prioritized endpoint";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


        public static void main(String[] args) throws IOException {





        server.setExecutor(null);
        server.start();
    }



    }
}
