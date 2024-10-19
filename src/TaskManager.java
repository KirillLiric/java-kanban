import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    void deleteSubtask(Subtask subtask);

    List<Task> getHistory();

    HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic);
}
