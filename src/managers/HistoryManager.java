package managers;

import task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void add(Task task);
    public ArrayList<Task> getHistory();
    public void remove(int id);
}
