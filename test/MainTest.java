import managers.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
   void testOfManagers() throws IOException {
        File tempFile = File.createTempFile("tempFile1", ".txt");
        assertTrue(Managers.getDefaultHistory() instanceof InMemoryHistoryManager, "Не инициализируется менеджер просмотров");
        assertTrue(Managers.getDefault(tempFile.toPath()) instanceof InMemoryTaskManager, "Не инициализируется менеджер задач");
   }

    //Создание временного файла
    @Test
    void checkProgram() throws IOException
    {
        File tempFile = File.createTempFile("tempFile1", ".txt");
        FileBackedTaskManager managerOfEmpty = Managers.getDefault(tempFile.toPath());
        assertTrue(managerOfEmpty.getEpicMap().isEmpty(), "Загрузка пустого файла происходит некорректно");

        //Запись в файл тестового текста
        String testText = "1,TASK,Task1,NEW,Description task1,\n2,EPIC,Epic2,DONE,Description epic2,\n" +
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(testText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager manager = Managers.getDefault(tempFile.toPath());
        manager.loadFromFile(tempFile);
        String[] massText = testText.split("\n");
        assertTrue(manager.getTaskMap().containsValue(manager.fromString(massText[0])), "Не импортирует задачу");

        Task testTask1 = new Task("Тестовая задача 1", "Описание тестовой задачи 1");
        Task testTask2 = new Task("Тестовая задача 2", "Описание тестовой задачи 2");
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        FileBackedTaskManager manager2 = Managers.getDefault(tempFile.toPath());
        assertEquals(3, manager2.getTaskMap().size(), "Не происходит сохранение");
    }

    //Проверка HistoryManager
    @Test
    void historyManagerTest() throws ManagerSaveException, IOException {

        File tempFile = File.createTempFile("tempFile1", ".txt");
        TaskManager manager = Managers.getDefault(tempFile.toPath());
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
    void checkIntegrity() throws ManagerSaveException, IOException {

        File tempFile = File.createTempFile("tempFile1", ".txt");
        TaskManager manager = Managers.getDefault(tempFile.toPath());

        Epic epic1 = new Epic("Тестовый эпик", "Эпик для проверки подзадач");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Первый подзадача", "Первая подзадача", epic1.getId());
        Subtask subtask2 = new Subtask("Вторая подзадача", "Вторая подзадача", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertTrue((epic1.getEpicSubtaskMap().containsKey(subtask1.getId()) &&
                epic1.getEpicSubtaskMap().containsKey(subtask2.getId())), "Эпик не хранит данные подзадач");


        manager.deleteSubtask(subtask1.getId());
        assertFalse(epic1.getEpicSubtaskMap().containsKey(subtask1.getId()), "Эпик хранит удаленную подзадачу");

        assertNotEquals(epic1.getId(), subtask1.getEpicID(), "Удаленная подзадача хранит id эпика");

    }
}






