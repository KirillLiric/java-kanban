import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Epic> epicMap = new HashMap<>();
     private HashMap<Integer, Task> taskMap = new HashMap<>();
     private HashMap<Integer, Task> subtaskMap = new HashMap<>();

    private int nextId = 0;

    //Получение списка задач

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, Task> getSubtaskMap() {
        return subtaskMap;
    }

    //Удаление всех задач

    public void deleteEpicMap() {
        epicMap.clear();
        subtaskMap.clear();
    }

    public void deleteTaskMap() {
        taskMap.clear();
    }

    public void deleteSubtaskMap() {
        subtaskMap.clear();
        for (Integer i : epicMap.keySet()) {
            epicMap.get(i).setStatus(Status.NEW);
        }
    }

    //Получение по id

    public Task getEpicFromMap(int id) {
        return epicMap.get(id);
    }

    public Task getTaskFromMap(int id) {
        return taskMap.get(id);
    }

    public Task getSubtaskFromMap(int id) {
        return subtaskMap.get(id);
    }

    //Создание

    public void addTask(Task task) {
        task.setId(nextId++);
        taskMap.put(task.getId(), task);

    }
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epicMap.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtaskMap.put(subtask.getId(), subtask);
        epicMap.get(subtask.getEpicID()).getSubtaskMap().put(subtask.getId(), subtask);
    }

    //Обновление

    public void updateTask(Task task) {
       taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> subtaskMap = epic.getSubtaskMap();
        int j = 0;
        int k = 0;
        if(subtaskMap.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer i : subtaskMap.keySet()) {
                if (Status.DONE.equals(subtaskMap.get(i).getStatus())) {
                    j++;
                } else if (Status.NEW.equals(subtaskMap.get(i).getStatus())) {
                    k++;
                }
            }
        }
        if(j == subtaskMap.size()) {
            epic.setStatus(Status.DONE);
        } else if (k == subtaskMap.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    //Получение списка всех подзадач эпика

    public HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic) {
        return epic.getSubtaskMap();
    }
}

