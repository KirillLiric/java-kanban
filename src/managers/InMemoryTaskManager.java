package managers;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    HashMap<Integer, Epic> epicMap = new HashMap<>();
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private final TreeSet<Task> prioritizedList = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    static int nextId = 0;

    public List<Task> getPrioritizedTasks() {
        return taskMap.values().stream()
                .filter(task -> task.getStartTime() != null) // Игнорируем задачи без времени начала
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    private boolean isOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime end2 = task2.getEndTime();
        return (task1.getStartTime().isBefore(end2) && end1.isAfter(task2.getStartTime()));
    }

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

    @Override
    public Task getEpicFromMap(int id) {
        try {
            historyManager.add(epicMap.get(id));
            return epicMap.get(id);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Нет эпика с таким id");
        }
    }

    @Override
    public Task getTaskFromMap(int id) {
        try {
            historyManager.add(taskMap.get(id));
            return taskMap.get(id);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Нет задачи с таким id");
        }
    }

    @Override
    public Task getSubtaskFromMap(int id) {
        try {
            historyManager.add(subtaskMap.get(id));
            return subtaskMap.get(id);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Нет подзадачи с таким id");
        }
    }

    @Override
    public void addTask(Task task) {
        if (!prioritizedList.stream()
                .anyMatch(streamTask -> isOverlap(task, streamTask))) {
            int id = nextId++;
            task.setId(id);
            taskMap.put(task.getId(), task);
            prioritizedList.add(task);
        } else {
            throw new ManagerSaveException("Пересечение по времени");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!prioritizedList.stream()
                .anyMatch(streamTask -> isOverlap(subtask, streamTask))) {
            subtask.setId(nextId++);
            subtaskMap.put(subtask.getId(), subtask);
            epicMap.get(subtask.getEpicID()).getEpicSubtaskMap().put(subtask.getId(), subtask);
            epicMap.get(subtask.getEpicID()).checkStatus();
            epicMap.get(subtask.getEpicID()).checkTime();
            prioritizedList.add(subtask);
        } else {
            throw new ManagerSaveException("Пересечение по времени");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!prioritizedList.stream().filter(streamTask -> !streamTask.equals(task))
                .anyMatch(streamTask -> isOverlap(task, streamTask))) {
            prioritizedList.remove(task);
            prioritizedList.add(task);
            taskMap.put(task.getId(), task);
        } else {
            throw new ManagerSaveException("Пересечение по времени");
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
        epic.checkStatus();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!prioritizedList.stream().filter(streamTask -> !streamTask.equals(epicMap.get(subtask.getEpicID())))
                .filter(streamTask -> !streamTask.equals(subtask))
                .anyMatch(streamTask -> isOverlap(subtask, streamTask))) {
            prioritizedList.remove(subtask);
            prioritizedList.add(subtask);
            subtaskMap.put(subtask.getId(), subtask);
            Epic epic = epicMap.get(subtask.getEpicID());
            HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
            epicSubtaskMap.put(subtask.getId(), subtask);
            epic.checkStatus();
            epic.checkTime();
        } else {
            throw new ManagerSaveException("Пересечение по времени");
        }
    }

    @Override
    public void deleteTask(int id) {
        prioritizedList.remove(taskMap.get(id));
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicMap.get(id);
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        for (Integer i : epicSubtaskMap.keySet()) {
            subtaskMap.remove(i);
            historyManager.remove(i);
        }
        epicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        prioritizedList.remove(subtaskMap.get(id));
        Subtask subtask = subtaskMap.get(id);
        Epic epic = epicMap.get(subtask.getEpicID());
        HashMap<Integer, Subtask> epicSubtaskMap = epic.getEpicSubtaskMap();
        epicSubtaskMap.remove(subtask.getId());
        epic.checkStatus();
        subtask.setNullEpicId();
        historyManager.remove(subtask.getId());
        subtaskMap.remove(subtask.getId());
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskMapFromEpic(Epic epic) {
        return epic.getEpicSubtaskMap();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public TreeSet<Task> getPrioritizedList() {
        return prioritizedList;
    }

}
