import com.sun.net.httpserver.HttpServer;
import managers.*;
import handlers.*;

import java.io.*;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final TaskManager manager;
    private HttpServer server;

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    HttpTaskServer(TaskManager inMemoryTaskManager) throws IOException {
        this.manager = inMemoryTaskManager;
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(manager));
            server.createContext("/subtasks", new SubtasksHandler(manager));
            server.createContext("/epics", new EpicsHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));

    }

    public static void main(String[] args) throws IOException {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(inMemoryTaskManager);
        httpTaskServer.server.start();
    }
}