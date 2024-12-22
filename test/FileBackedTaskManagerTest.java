import managers.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import managers.Managers;
import org.junit.jupiter.api.Test;
import task.Task;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager>{

    private File tempFile = File.createTempFile("tempFile", ".txt");
    private FileBackedTaskManager manager = Managers.getDefaultFileBackedTaskManager(tempFile.toPath());

    public FileBackedTaskManagerTest() throws IOException {
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return manager;
    }

    @Test
    public void overlapTest() {
        Task task1 = new Task("TestTask1", "Description1");
        task1.setStartTime("2024-12-20T12:00:00.000000");
        task1.setDuration("15");
        Task task2 = new Task("TestTask2", "Description2");
        task2.setStartTime("2024-12-20T12:00:00.000000");
        task2.setDuration("15");
        Task task3 = new Task("TestTask3", "Description3");
        task3.setStartTime("2024-12-22T12:30:00.000000");
        task3.setDuration("15");

        manager.addTask(task1);
        manager.addTask(task2);
        assertEquals(1, manager.getPrioritizedTasks().size(), "Добавляет пересекающиеся по времени " +
                "задачи");

        manager.addTask(task3);
        assertEquals(2, manager.getPrioritizedTasks().size(), "Добавление задачи не происходит");
    }


}
