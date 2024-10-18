import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    //Проверка работоспособности equals у задач

    @Test
    void equalsOfTasks() {
        Task task1 = new Task("Первая задача", "Первая задача");
        Task task2 = new Task("Вторая задача", "Вторая задача");
        task1.setId(17);
        task2.setId(17);
        assertTrue(task1.equals(task2), "Тест на эквивалентность задач провален");
    }

    @Test
    void equalsOfEpics() {
        Epic epic1 = new Epic("Первый эпик", "Первый эпик");
        Epic epic2 = new Epic("Второй эпик", "Второй эпик");
        epic1.setId(17);
        epic2.setId(17);
        assertTrue(epic1.equals(epic2), "Тест на эквивалентность эпиков провален");
    }

    @Test
    void equalsOfSubtask() {
        Epic epic1 = new Epic("Тестовый эпик", "Эпик для проверки подзадач");
        epic1.setId(17);
        Subtask subtask1 = new Subtask("Первый подзадача", "Первая подзадача", epic1.getId());
        Subtask subtask2 = new Subtask("Вторая подзадача", "Вторая подзадача", epic1.getId());
        subtask1.setId(17);
        subtask2.setId(17);
        assertTrue(subtask1.equals(subtask2), "Тест на эквивалентность подзадач провален");
    }

    //Проверка Эпика
    @Test
    void checkEpic() {
        Epic epic1 = new Epic("Тестовый эпик", "Эпик для проверки подзадач");
        Managers.getDefault().addEpic(epic1);


    }

    //Проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    void testOfManagers() {
        assertTrue(Managers.getDefaultHistory() instanceof InMemoryHistoryManager, "Не инициализируется менеджер просмотров");
        assertTrue(Managers.getDefault() instanceof InMemoryTaskManager, "Не инициализируется менеджер задач");
    }

    //Неизменность задачи при добавлении задачи в менеджер
    @Test
    void immutabilityTask() {
        String name = "Имя";
        String description = "Описание";
        Task task = new Task(name, description);
        Managers.getDefault().addTask(task);
        HashMap<Integer, Task> map = Managers.getDefault().getTaskMap();
        Task task1 = map.get(task.getId());
        assertEquals(name, task1.getName(), "Поменялось имя");
        assertEquals(description, task1.getDescription(), "Поменялось описание");
    }

    //Проверка InMemoryTaskManager
    @Test
    void inMemoryTaskManagerTest() {
        Task testTask = new Task("Тестовая задача", "Тестовая задача");
        Managers.getDefault().addTask(testTask);
        Epic testEpic = new Epic("Тестовый эпик", "Тестовый эпик");
        Managers.getDefault().addEpic(testEpic);
        Subtask testSubtask = new Subtask("Тестовая подзадача", "Тестовая подзадача", testEpic.getId());
        Managers.getDefault().addSubtask(testSubtask);
        assertNotNull(Managers.getDefault().getTaskFromMap(testTask.getId()), "Задача не возвращается");
        assertNotNull(Managers.getDefault().getEpicFromMap(testEpic.getId()), "Эпик не возвращается");
        assertNotNull(Managers.getDefault().getSubtaskFromMap(testSubtask.getEpicID()), "Подзадача не возвращается");
        //Managers.getDefault().getTaskFromMap(testTask.getId())
    }

    //Проверка HistoryManager
    @Test
    void historyManagerTest() {
        String taskName = "Старая тестовая задача";
        String taskDescription = "Описание старой задачи";
        String newTaskName = "Новая тестовая задача";
        String newTaskDescription = "Описание новой задачи";

        Task testTask = new Task(taskName, taskDescription);
        Managers.getDefault().addTask(testTask);
        Managers.getDefaultHistory().add(testTask);

        Task newTestTask = new Task(newTaskName, newTaskDescription);
        newTestTask.setId(testTask.getId());
        Managers.getDefault().updateTask(newTestTask);
        Managers.getDefaultHistory().add(newTestTask);

        String name = Managers.getDefaultHistory().getHistory().getFirst().getName();
        String description = Managers.getDefaultHistory().getHistory().getFirst().getDescription();

        assertEquals(taskName, name, "Не сохраняет имя старой задачи");
        assertEquals(taskDescription, description, "Не сохраняет описание старой задачи");
    }
}