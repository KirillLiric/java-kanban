package managers;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int nextId = 0;

    //Получение списка задач
    @Override
    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    @Override
    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskMap() {
        return subtaskMap;
    }

    //Удаление всех задач
    @Override
    public void deleteEpicMap() {
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteTaskMap() {
        taskMap.clear();
    }

    @Override
    public void deleteSubtaskMap() {
        subtaskMap.clear();
        for (Integer i : epicMap.keySet()) {
            epicMap.get(i).setStatus(Status.NEW);
        }
    }

    //Получение по id

    @Override
    public Task getEpicFromMap(int id) {
        historyManager.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public Task getTaskFromMap(int id) {
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Task getSubtaskFromMap(int id) {
        historyManager.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    //Создание

    @Override
    public void addTask(Task task) {
        int id = nextId++;
        task.setId(id);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtaskMap.put(subtask.getId(), subtask);
        epicMap.get(subtask.getEpicID()).getEpicSubtaskMap().put(subtask.getId(), subtask);
    }

    //Обновление

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        checkEpicStatus(epic, epicSubtaskMap);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        epicSubtaskMap.put(subtask.getId(), subtask);
        checkEpicStatus(epic, epicSubtaskMap);
    }

    //Удаление по id

    @Override
    public void deleteTask(int id) {
        taskMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicMap.get(id);
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        for (Integer i : epicSubtaskMap.keySet()) {
            subtaskMap.remove(i);
        }
        epicMap.remove(id);
    }

    @Override
    public void deleteSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        epicSubtaskMap.remove(subtask.getId());
        checkEpicStatus(epic, epicSubtaskMap);
        subtaskMap.remove(subtask.getId());
    }


    //Получение списка всех подзадач эпика

    @Override
    public HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic) {
        return epic.getEpicSubtaskMap();
    }

    //Получение истории

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    //Дополнительные методы

    //Проверка статуса эпика

    public void checkEpicStatus(Epic epic, HashMap<Integer, Subtask> epicSubtaskMap) {
        epicSubtaskMap = epic.getEpicSubtaskMap();
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

}
