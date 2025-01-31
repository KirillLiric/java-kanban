import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.*;
import task.Epic;
import task.Status;
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

            switch(method) {

                case "GET":
                    System.out.println("/GET");
                    if (arrayPath.length == 2) {
                        response = gson.toJson(fileBackedTaskManager.getTaskMap());
                        sendText(exchange, response, 200);
                    } else if (arrayPath.length == 3) {
                        String value = arrayPath[2].substring(1, arrayPath[2].length() - 1);
                        try {
                            response = gson.toJson(fileBackedTaskManager.getTaskFromMap(Integer.parseInt(value)));
                            sendText(exchange, response, 200);
                        } catch (Exception e) {
                            sendNotFound(exchange, value);
                        }
                    }

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
                    String value = arrayPath[2].substring(1, arrayPath[2].length() - 1);
                    fileBackedTaskManager.deleteTask(Integer.parseInt(value));
                    sendText(exchange, "", 200);

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
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            int option;
            String response = "";

            try {
                String stringOption = path.split("/")[2];
                String value = stringOption.substring(1, stringOption.length()-1);
                option = Integer.parseInt(value);
            } catch (ArrayIndexOutOfBoundsException e) {
                option = -1;
            }

            switch (method) {
                case "GET":
                    try {
                        if (option == -1) {
                            response = gson.toJson(fileBackedTaskManager.getTaskMap());
                        } else {
                            response = gson.toJson(fileBackedTaskManager.getTaskFromMap(option));
                        }
                        sendText(exchange, response, 200);
                    } catch (RuntimeException e) {
                        sendNotFound(exchange, "Subtask");
                    }
                    break;

                case "POST":
                    String stringTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        if (option == -1) {
                            fileBackedTaskManager.addTask(FileBackedTaskManager.fromString(stringTask));
                        } else {
                            fileBackedTaskManager.updateTask(FileBackedTaskManager.fromString(stringTask));
                        }
                        sendText(exchange, "{message: Subtask processed successfully.}", 200);
                    } catch (ManagerSaveException e) {
                        sendHasInteractions(exchange, "Subtask");
                    }
                    break;

                case "DELETE":
                    fileBackedTaskManager.deleteTask(option);
                    sendText(exchange, "{message: Subtask deleted successfully.}", 200);
                    break;

                default:
                    sendText(exchange, "{error: Method not allowed.}", 405);
                    break;
            }
        }
    }

    // Обработчик для /epics
    class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            int option;
            String response = "";

            try {
                String stringOption = path.split("/")[2];
                String value = stringOption.substring(1, stringOption.length()-1);
                option = Integer.parseInt(value);
            } catch (ArrayIndexOutOfBoundsException e) {
                option = -1;
            }

            switch (method) {
                case "GET":
                    try {
                        if (option == -1) {
                            response = gson.toJson(fileBackedTaskManager.getEpicMap());
                        } else {
                            if(path.split("/").length >= 3 && path.split("/")[3].equals("subtasks")) {
                                Epic epic = (Epic) fileBackedTaskManager.getEpicFromMap(option);
                                response = gson.toJson(epic.getEpicSubtaskMap());
                            } else {
                                response = gson.toJson(fileBackedTaskManager.getEpicFromMap(option));
                            }
                        }
                        sendText(exchange, response, 200);
                    } catch (RuntimeException e) {
                        sendNotFound(exchange, "Epic");
                    }
                    break;

                case "POST":
                    String stringTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        if (option == -1) {
                            fileBackedTaskManager.addEpic((Epic)FileBackedTaskManager.fromString(stringTask));
                        } else {
                            fileBackedTaskManager.updateEpic((Epic)FileBackedTaskManager.fromString(stringTask));
                        }
                        sendText(exchange, "{message: Subtask processed successfully.}", 200);
                    } catch (ManagerSaveException e) {
                        sendHasInteractions(exchange, "Subtask");
                    }
                    break;

                case "DELETE":
                    fileBackedTaskManager.deleteTask(option);
                    sendText(exchange, "{message: Subtask deleted successfully.}", 200);
                    break;

                default:
                    sendText(exchange, "{error: Method not allowed.}", 405);
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
            switch (method) {
                case "GET":
                    response = gson.toJson(fileBackedTaskManager.getHistory());
                    break;

                default:
                    response = "Такого метода нет";
                    break;
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
            switch (method) {
                case "GET":
                    response = gson.toJson(fileBackedTaskManager.getPrioritizedTasks());
                    break;
                default:
                    response = "Такого метода нет";
                    break;
            }
            sendText(exchange, response, 200);
        }

    }

    public static void main(String[] args) throws IOException {

//        class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
//            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//
//            @Override
//            public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
//                return new JsonPrimitive(localDateTime.format(formatter));
//            }
//
//            @Override
//            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                return LocalDateTime.parse(json.getAsString(), formatter);
//            }
//        }
//
//        class DurationAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
//            @Override
//            public JsonElement serialize(Duration duration, Type typeOfSrc, JsonSerializationContext context) {
//                return new JsonPrimitive(duration.getSeconds());
//            }
//
//            @Override
//            public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                return Duration.ofSeconds(json.getAsLong());
//            }
//        }
//
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
//                .registerTypeAdapter(Duration.class, new DurationAdapter())
//                .create();
//
//        Task task = new Task("Test 2", "Testing task 2",
//                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
//
//        gson.toJson(task);
//        System.out.println(gson.toJson(task));
//
//        Task testTask = gson.fromJson(gson.toJson(task), Task.class);
//
//        System.out.println(testTask.getName());
//        System.out.println(testTask.getStartTime());

        File file = File.createTempFile("tempFile", ".txt");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        TaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(file.toPath());
        HttpTaskServer httpTaskServer = new HttpTaskServer((InMemoryTaskManager) inMemoryTaskManager,
                (FileBackedTaskManager) fileBackedTaskManager);
        httpTaskServer.server.start();


    }
}