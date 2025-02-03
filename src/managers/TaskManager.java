package managers;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    HashMap<Integer, Epic> getEpicMap();

    HashMap<Integer, Task> getTaskMap();

    HashMap<Integer, Subtask> getSubtaskMap();

    void deleteEpicMap();

    void deleteTaskMap();

    void deleteSubtaskMap();

    Task getEpicFromMap(int id);

    Task getTaskFromMap(int id);

    Task getSubtaskFromMap(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedList();

    HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic);

}
