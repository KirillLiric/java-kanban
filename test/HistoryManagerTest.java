import managers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private static File tempFile;

    @Test
    void testOfManagers() throws IOException {
        tempFile = File.createTempFile("tempFile", ".txt");
        assertTrue(Managers.getDefaultHistory() instanceof InMemoryHistoryManager, "Не инициализируется менеджер просмотров");
        assertTrue(Managers.getDefaultFileBackedTaskManager(tempFile.toPath()) instanceof InMemoryTaskManager,
                "Не инициализируется менеджер задач");
    }

    @Test
    void historyManagerTest() throws ManagerSaveException, IOException{

        tempFile = File.createTempFile("tempFile", ".txt");
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefaultFileBackedTaskManager(tempFile.toPath());


        Task task1 = new Task("Первая задача", "Первая задача");
        task1.setStartTime("2024-12-20T12:50:00.000000");
        task1.setDuration("15");
        Task task2 = new Task("Вторая задача", "Вторая задача");
        task2.setStartTime("2024-12-20T15:00:00.000000");
        task2.setDuration("15");
        Task task3 = new Task("Третья задача", "Третья задача");
        task3.setStartTime("2024-12-20T15:30:00.000000");
        task3.setDuration("15");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(historyManager.getHistory().size(), 3, "Операция добавления работает некорректно");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(historyManager.getHistory().size(), 3, "Хранит задачи с одним Id");

        historyManager.remove(task1.getId());
        assertEquals(historyManager.getHistory().size(), 2, "Некорректно удаляет задачи");

        assertNotNull(historyManager.getHistory(), "История не возвращается");

    }
}
