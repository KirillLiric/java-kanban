import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    private int nextId = 0;

    //Получение списка задач

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, Subtask> getSubtaskMap() {
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
        epicMap.get(subtask.getEpicID()).getEpicSubtaskMap().put(subtask.getId(), subtask);
    }

    //Обновление

    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        int j = 0;
        int k = 0;
        if (epicSubtaskMap.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer i : epicSubtaskMap.keySet()) {
                if (Status.DONE.equals(epicSubtaskMap.get(i).getStatus())) {
                    j++;
                } else if (Status.NEW.equals(epicSubtaskMap.get(i).getStatus())) {
                    k++;
                }
            }
        }
        if (j == epicSubtaskMap.size()) {
            epic.setStatus(Status.DONE);
        } else if (k == epicSubtaskMap.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        epicSubtaskMap.put(subtask.getId(), subtask);
        int j = 0;
        int k = 0;
        if (epicSubtaskMap.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer i : epicSubtaskMap.keySet()) {
                if (Status.DONE.equals(epicSubtaskMap.get(i).getStatus())) {
                    j++;
                } else if (Status.NEW.equals(epicSubtaskMap.get(i).getStatus())) {
                    k++;
                }
            }
        }
        if (j == epicSubtaskMap.size()) {
            epic.setStatus(Status.DONE);
        } else if (k == epicSubtaskMap.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    //Удаление по id

    public void deleteTask(int id) {
        taskMap.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epicMap.get(id);
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        for (Integer i : epicSubtaskMap.keySet()) {
            subtaskMap.remove(i);
        }
        epicMap.remove(id);
    }

    public void deleteSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        epicSubtaskMap.remove(subtask.getId());
        int j = 0;
        int k = 0;
        if (epicSubtaskMap.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer i : epicSubtaskMap.keySet()) {
                if (Status.DONE.equals(epicSubtaskMap.get(i).getStatus())) {
                    j++;
                } else if (Status.NEW.equals(epicSubtaskMap.get(i).getStatus())) {
                    k++;
                }
            }
        }
        if (j == epicSubtaskMap.size()) {
            epic.setStatus(Status.DONE);
        } else if (k == epicSubtaskMap.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        subtaskMap.remove(subtask.getId());
    }


    //Получение списка всех подзадач эпика

    public HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic) {
        return epic.getEpicSubtaskMap();
    }
}

