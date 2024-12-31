import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import managers.TaskManager;
import task.*;


import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description");
        task.setStartTime("2024-12-26T12:56:01.918810");
        task.setDuration("15");
        taskManager.addTask(task);
        assertNotNull(taskManager.getTaskMap().get(task.getId()));
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Test Task", "Description");
        task.setStartTime("2024-12-26T12:56:01.918810");
        task.setDuration("15");
        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTaskMap().get(task.getId()), "Не происходит удаление задачи");
    }

    @Test
    public void testUpdateTask() {

        Task task = new Task("Test Task", "Description");
        task.setStartTime("2024-12-26T12:56:01.918810");
        task.setDuration("15");
        taskManager.addTask(task);

        Task task1 = new Task("Test Task", "Updated Task");
        task1.setStartTime("2024-12-26T12:56:01.918810");
        task1.setDuration("15");
        task1.setId(task.getId());
        taskManager.updateTask(task1);

        assertEquals("Updated Task", taskManager.getTaskMap().get(task1.getId()).getDescription(),
                "Задача не обновляется");
    }

    @Test
    public void testAddSubtaskWithEpicCheck() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getId());
        subtask.setStartTime("2024-12-26T12:56:01.918810");
        subtask.setDuration("15");
        taskManager.addSubtask(subtask);
        assertNotNull(taskManager.getSubtaskMap().get(subtask.getId()), "Не происходит добавление подзадачи");
        assertEquals(epic.getId(), subtask.getEpicID(), "Подзадача хранит некорректный номер эпика");
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic("Test Epic", "Epic Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStartTime("2024-12-26T12:56:01.918810");
        subtask1.setDuration("15");
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStartTime("2024-12-27T12:56:01.918810");
        subtask2.setDuration("15");

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "При статусе всех подзадач NEW эпик имеет другой статус");

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Не происходит изменение статуса эпика");
    }

}
