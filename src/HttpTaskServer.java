import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.*;
import task.Epic;
import task.Status;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {

    private static final Gson gson = new Gson();
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
            server.createContext("/hello", new HelloHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    public class BaseHttpHandler {

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

    class HelloHandler extends BaseHttpHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /hello запроса от клиента.");

            String response = "Kir! Glad to see you on our server.";
            httpExchange.sendResponseHeaders(200, 0);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }


    // Обработчик для /tasks
    class TasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.toString();
            String method = exchange.getRequestMethod();
            System.out.println(path.split("/")[2]);
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
                        sendNotFound(exchange, "Task");
                    }
                    break;

                case "POST":
                    String stringTask = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        if (option == -1) {
                            try {
                                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                                 Task newTask = gson.fromJson(isr, Task.class);
                                fileBackedTaskManager.addTask(newTask);
                                sendText(exchange, gson.toJson(newTask), 201); // 201 Created
                            } catch (RuntimeException e) {
                                sendText(exchange, "{Ошибка: Не удалось создать задачу.}", 400); // Bad Request
                            }
                        } else {
                            fileBackedTaskManager.updateTask(FileBackedTaskManager.fromString(stringTask));
                        }
                        sendText(exchange, "{message: Task processed successfully.}", 200);
                    } catch (RuntimeException e) {
                        sendHasInteractions(exchange, "Task");
                    }
                    break;

                case "DELETE":
                    fileBackedTaskManager.deleteTask(option);
                    sendText(exchange, "{message: Task deleted successfully.}", 200);
                    break;
                default:
                    sendText(exchange, "{error: Method not allowed.}", 405);
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

        class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        }

        class DurationAdapter implements JsonSerializer<Duration> {
            @Override
            public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.toMinutes());
            }
        }

        File file = File.createTempFile("tempFile", ".txt");
        TaskManager inMemoryTaskManager = Managers.getDefault();
        TaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(file.toPath());
        HttpTaskServer httpTaskServer = new HttpTaskServer((InMemoryTaskManager) inMemoryTaskManager,
                (FileBackedTaskManager) fileBackedTaskManager);

        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String taskJson = gson.toJson(task);
        System.out.println(taskJson);

        httpTaskServer.server.start();
        System.out.println("Сервер запущен на 8080 порту");
    }
}