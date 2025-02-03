import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import managers.*;
import task.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskServerTest {


    File file;
    {
        try {
            file = File.createTempFile("tempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskServerTest() throws IOException {
    }

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
            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServerTest.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new HttpTaskServerTest.DurationAdapter())
            .create();

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
    HttpTaskServer taskServer = new HttpTaskServer(inMemoryTaskManager);

    @BeforeEach
    public void setUp() {
        inMemoryTaskManager.deleteTaskMap();
        inMemoryTaskManager.deleteSubtaskMap();
        inMemoryTaskManager.deleteEpicMap();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();

    }

    @Test
    public void testTask() throws IOException, InterruptedException {
        // Создаём задачу
        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // Конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // Создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка на создание
        assertEquals(201, response.statusCode(), "Задача не создаётся");

        // Проверяем, что создалась одна задача с корректным именем
        //fileBackedTaskManager
        List<Task> listTask =  List.copyOf(fileBackedTaskManager.getTaskMap().values());

        assertNotNull(listTask, "Задачи не возвращаются");
        //assertEquals(1, listTask.size(), "Некорректное количество задач");
        //assertEquals("Test 1", listTask.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testOverlapTask() throws IOException, InterruptedException {
        // Создаём задачи
        Task task1 = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        // Конвертируем их в JSON
        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        // Создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // Проверка на создание
        assertEquals(201, response1.statusCode(), "Задача не создаётся");

        //Проверка на пересечение
        assertEquals(406, response2.statusCode(), "Не генерируется исключение");

        // Проверяем, что создалась одна задача с корректным именем
        //fileBackedTaskManager
        List<Task> listTask =  List.copyOf(fileBackedTaskManager.getTaskMap().values());

        assertEquals(1, listTask.size(), "Некорректное количество задач");
        assertEquals("Test 1", listTask.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {

        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // Конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // Создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверка на создание
        assertEquals(201, response.statusCode(), "Задача не создаётся");

        List<Task> listTask =  List.copyOf(fileBackedTaskManager.getTaskMap().values());
        int id = listTask.get(0).getId();

        String text = String.format("http://localhost:8080/tasks/%d", id);

       URI urlDelete = URI.create(text);
       HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();

       HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
       assertEquals(200, responseDelete.statusCode(), "Задача удаляется");

    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task updateTask = new Task("Test 1", "Update task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        // Конвертируем её в JSON
        String taskJson = gson.toJson(task);


        // Создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers
                .ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Проверка на создание
        assertEquals(201, response.statusCode(), "Задача не создаётся");

        List<Task> listTask =  List.copyOf(fileBackedTaskManager.getTaskMap().values());
        int id = listTask.get(0).getId();
        updateTask.setId(id);
        String updateTaskJson = gson.toJson(updateTask);
        String text = String.format("http://localhost:8080/tasks/%d", id);
        URI urlUpdate = URI.create(text);
        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(urlUpdate).POST(HttpRequest.BodyPublishers
                .ofString(updateTaskJson)).build();
        HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseUpdate.statusCode(), "Задача не обновляется");

        List<Task> updateListTask =  List.copyOf(fileBackedTaskManager.getTaskMap().values());
        assertEquals(1, updateListTask.size(), "Сохраняет обе задачи");
        assertEquals("Update task", updateListTask.get(0).getDescription());

    }

    @Test
    public void testEpicAndSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Epic 1", "test epic");
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI urlEpic = URI.create("http://localhost:8080/epics");
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(HttpRequest.BodyPublishers
                .ofString(epicJson)).build();
        HttpResponse<String> responseEpic = client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        // Проверка на создание
        assertEquals(201, responseEpic.statusCode(), "Эпик не создаётся");

        List<Task> listEpic =  List.copyOf(fileBackedTaskManager.getEpicMap().values());
        int id = listEpic.get(0).getId();

        Subtask subtask = new Subtask("Subtask 1", "test subtask", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), id);
        String subtaskJson = gson.toJson(subtask);
        URI urlSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestSubtask = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers
                .ofString(subtaskJson)).build();
        HttpResponse<String> responseSubtask = client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, responseSubtask.statusCode(), "Подзадача не создаётся");

        assertEquals(1, fileBackedTaskManager.getEpicMap().get(id).getEpicSubtaskMap().size(),
                "Не сохраняет подзадачу в массив эпика");

        Subtask subtask2 = new Subtask("Subtask 2", "test subtask 2", Status.NEW, Duration.ofMinutes(5),
                LocalDateTime.now(), id);
        String subtaskJson2 = gson.toJson(subtask2);
        HttpRequest requestSubtask2 = HttpRequest.newBuilder().uri(urlSubtask).POST(HttpRequest.BodyPublishers
                .ofString(subtaskJson2)).build();
        HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, responseSubtask2.statusCode(), "Происходит перекрытие");

    }
}
