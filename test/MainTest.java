import managers.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.HashMap;

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
        TaskManager manager = Managers.getDefault();
        String name = "Имя";
        String description = "Описание";
        Task task = new Task(name, description);
        manager.addTask(task);
        HashMap<Integer, Task> map = manager.getTaskMap();
        Task task1 = map.get(task.getId());
        assertEquals(name, task1.getName(), "Поменялось имя");
        assertEquals(description, task1.getDescription(), "Поменялось описание");
    }

    //Проверка InMemoryTaskManager
    @Test
    void inMemoryTaskManagerTest() {
        TaskManager manager = Managers.getDefault();
        Task testTask = new Task("Тестовая задача", "Тестовая задача");
        manager.addTask(testTask);
        Epic testEpic = new Epic("Тестовый эпик", "Тестовый эпик");
        manager.addEpic(testEpic);
        Subtask testSubtask = new Subtask("Тестовая подзадача", "Тестовая подзадача", testEpic.getId());
        manager.addSubtask(testSubtask);
        assertNotNull(manager.getTaskFromMap(testTask.getId()), "Задача не возвращается");
        assertNotNull(manager.getEpicFromMap(testEpic.getId()), "Эпик не возвращается");
        assertNotNull(manager.getSubtaskFromMap(testSubtask.getId()), "Подзадача не возвращается");

    }

    //Проверка HistoryManager
    @Test
    void historyManagerTest() {

        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Проверка на хранение задач с одним и тем же id
        Task task1 = new Task("Первая задача", "Первая задача");
        Task task2 = new Task("Вторая задача", "Вторая задача");
        Task task3 = new Task("Третья задача", "Третья задача");

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

    //Проверка целостности задач
    @Test
    void checkIntegrity() {

        TaskManager manager = Managers.getDefault();

        Epic epic1 = new Epic("Тестовый эпик", "Эпик для проверки подзадач");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Первый подзадача", "Первая подзадача", epic1.getId());
        Subtask subtask2 = new Subtask("Вторая подзадача", "Вторая подзадача", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertTrue((epic1.getEpicSubtaskMap().containsKey(subtask1.getId()) &&
                epic1.getEpicSubtaskMap().containsKey(subtask2.getId())), "Эпик не хранит данные подзадач");


        manager.deleteSubtask(subtask1);
        assertFalse(epic1.getEpicSubtaskMap().containsKey(subtask1.getId()), "Эпик хранит удаленную подзадачу");

        assertNotEquals(epic1.getId(), subtask1.getEpicID(), "Удаленная подзадача хранит id эпика");

    }

}