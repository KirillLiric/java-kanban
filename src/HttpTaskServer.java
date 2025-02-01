import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
        @Override
        public JsonElement serialize(Duration duration, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(duration.getSeconds());
        }

        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Duration.ofSeconds(json.getAsLong());
        }
    }

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    private final InMemoryTaskManager inMemoryTaskManager;
    private final FileBackedTaskManager fileBackedTaskManager;
    HttpServer server;

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    HttpTaskServer(InMemoryTaskManager inMemoryTaskManager, FileBackedTaskManager fileBackedTaskManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
        this.fileBackedTaskManager = fileBackedTaskManager;

        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler());
            server.createContext("/subtasks", new SubtasksHandler());
            server.createContext("/epics", new EpicsHandler());
            server.createContext("/history", new HistoryHandler());
            server.createContext("/prioritized", new PrioritizedHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public class BaseHttpHandler {

        protected String readRequestBody(HttpExchange exchange) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder requestBodyBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
            return requestBodyBuilder.toString();
        }

        protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
            byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
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
    }

    // Обработчик для /tasks
    class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String[] arrayPath = path.split("/");
            String response;
            switch(method){
                case "GET":
                    System.out.println("/GET");
                    if (arrayPath.length == 2) {
                        response = gson.toJson(fileBackedTaskManager.getTaskMap());
                        sendText(exchange, response, 200);
                    } else if (arrayPath.length == 3) {
                        String value = arrayPath[2];
                        try {
                            response = gson.toJson(fileBackedTaskManager.getTaskFromMap(Integer.parseInt(value)));
                            sendText(exchange, response, 200);
                        } catch (Exception e) {
                            sendNotFound(exchange, value);
                        }
                    }
                    break;
                case "POST":
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
                            fileBackedTaskManager.addTask(task);
                            sendText(exchange, "", 201);
                        } catch (RuntimeException e) {
                            sendHasInteractions(exchange, task.getName());
                        }
                    } else {
                        try {
                            fileBackedTaskManager.updateTask(task);
                            sendText(exchange, "", 201);
                        } catch (RuntimeException e) {
                            sendHasInteractions(exchange, task.getName());
                        }
                    }
                    break;
                case "DELETE":
                    System.out.println("/DELETE");
                    String value = arrayPath[2];
                    fileBackedTaskManager.deleteTask(Integer.parseInt(value));
                    sendText(exchange, "", 200);
                    break;
                default:
                    sendText(exchange, "{error: Метод не поддерживается.}", 405);
                    break;
            }
        }
    }

    // Обработчик для /subtasks
    class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /subtasks запроса от клиента.");
            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String[] arrayPath = path.split("/");
            String response;
            switch(method){
                case "GET":
                    System.out.println("/GET");
                    if (arrayPath.length == 2) {
                        response = gson.toJson(fileBackedTaskManager.getSubtaskMap());
                        sendText(exchange, response, 200);
                    } else if (arrayPath.length == 3) {
                        String value = arrayPath[2];
                        try {
                            response = gson.toJson(fileBackedTaskManager.getSubtaskFromMap(Integer.parseInt(value)));
                            sendText(exchange, response, 200);
                        } catch (Exception e) {
                            sendNotFound(exchange, value);
                        }
                    }
                    break;

                case "POST":
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
                            fileBackedTaskManager.addSubtask(task);
                            sendText(exchange, "", 201);
                        } catch (RuntimeException e) {
                            sendHasInteractions(exchange, task.getName());
                        }
                    } else {
                        try {
                            fileBackedTaskManager.updateSubtask(task);
                            sendText(exchange, "", 201);
                        } catch (RuntimeException e) {
                            sendHasInteractions(exchange, task.getName());
                        }
                    }
                    break;

                case "DELETE":
                    System.out.println("/DELETE");
                    String value = arrayPath[2];
                    fileBackedTaskManager.deleteSubtask(Integer.parseInt(value));
                    sendText(exchange, "", 200);
                    break;

                default:
                    sendText(exchange, "{error: Метод не поддерживается.}", 405);
                    break;
            }
        }
    }

    // Обработчик для /epics
    class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /epics запроса от клиента.");
            String method = exchange.getRequestMethod();
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String[] arrayPath = path.split("/");
            String response;
            switch(method){
                case "GET":
                    System.out.println("/GET");
                    if (arrayPath.length == 2) {
                        response = gson.toJson(fileBackedTaskManager.getEpicMap());
                        sendText(exchange, response, 200);
                    } else if (arrayPath.length == 3) {
                        String value = arrayPath[2];
                        try {
                            response = gson.toJson(fileBackedTaskManager.getEpicFromMap(Integer.parseInt(value)));
                            sendText(exchange, response, 200);
                        } catch (Exception e) {
                            sendNotFound(exchange, value);
                        }
                    } else if ((arrayPath.length == 4) && (arrayPath[3].equals("subtasks"))) {
                        String value = arrayPath[2];
                        try {
                            Epic epic = (Epic) fileBackedTaskManager.getEpicFromMap(Integer.parseInt(value));
                            response = gson.toJson(epic.getEpicSubtaskMap());
                            sendText(exchange, response, 200);
                        } catch (Exception e) {
                            sendNotFound(exchange, value);
                        }
                    }
                    break;

                case "POST":
                    System.out.println("/POST");
                    String requestBody = readRequestBody(exchange);
                    Epic epic = gson.fromJson(requestBody, Epic.class);
                    fileBackedTaskManager.addEpic(epic);
                    sendText(exchange, "", 201);
                    break;

                case "DELETE":
                    System.out.println("/DELETE");
                    String value = arrayPath[2];
                    fileBackedTaskManager.deleteEpic(Integer.parseInt(value));
                    sendText(exchange, "", 200);
                    break;

                default:
                    sendText(exchange, "{error: Метод не поддерживается.}", 405);
                    break;
            }
        }
    }

    // Обработчик для /history
    class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            if (method.equals("GET")) {
                response = gson.toJson(fileBackedTaskManager.getHistory());
            } else {
                response = "Такого метода нет";
            }
            sendText(exchange, response, 200);
        }
    }

    // Обработчик для /prioritized
    class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response;
            if (method.equals("GET")) {
                response = gson.toJson(fileBackedTaskManager.getPrioritizedTasks());
            } else {
                response = "Такого метода нет";
            }
            sendText(exchange, response, 200);
        }
    }

    public static void main(String[] args) throws IOException {

        File file = File.createTempFile("tempFile", ".txt");
        TaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(file.toPath());
        TaskManager inMemoryTaskManager = Managers.getDefault();

        HttpTaskServer httpTaskServer = new HttpTaskServer((InMemoryTaskManager) inMemoryTaskManager,
                (FileBackedTaskManager) fileBackedTaskManager);
        httpTaskServer.server.start();
    }
}