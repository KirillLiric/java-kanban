package managers;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    HashMap<Integer, Epic> getEpicMap();

    HashMap<Integer, Task> getTaskMap();

    HashMap<Integer, Subtask> getSubtaskMap();

    void deleteEpicMap() throws ManagerSaveException;

    void deleteTaskMap() throws ManagerSaveException;

    void deleteSubtaskMap() throws ManagerSaveException;

    Task getEpicFromMap(int id);

    Task getTaskFromMap(int id);

    Task getSubtaskFromMap(int id);

    void addTask(Task task) throws ManagerSaveException;

    void addEpic(Epic epic) throws ManagerSaveException;

    void addSubtask(Subtask subtask) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void deleteTask(int id) throws ManagerSaveException;

    void deleteEpic(int id) throws ManagerSaveException;

    void deleteSubtask(int id) throws ManagerSaveException;

    List<Task> getHistory();

    HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic);
}
