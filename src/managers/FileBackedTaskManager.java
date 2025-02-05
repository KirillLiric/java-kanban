package managers;

import task.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path saveFile;


    public FileBackedTaskManager(Path saveFile) {
        this.saveFile = saveFile;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.toFile()))) {
            for (Task task : taskMap.values()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic task : epicMap.values()) {
                writer.write(task.toString() + "\n");
            }
            for (Subtask task : subtaskMap.values()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении");
        }
    }

    public static Task fromString(String value) {
        String[] taskText = value.split(",");
        Task task;
        if (Tasks.TASK.toString().equals(taskText[1])) {
            task = new Task(taskText[2], taskText[4]);
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
            task.setStartTime(taskText[5]);
            task.setDuration(taskText[6]);
        } else if (Tasks.EPIC.toString().equals(taskText[1])) {
            task = new Epic(taskText[2], taskText[4]);
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
        } else if (Tasks.SUBTASK.toString().equals(taskText[1])) {
            task = new Subtask(taskText[2], taskText[4], Integer.parseInt(taskText[5]));
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
            task.setStartTime(taskText[6]);
            task.setDuration(taskText[7]);
        } else {
            task = null;
        }
        return task;
    }

    private static Status getStatus(String value) {
        if (Status.NEW.toString().equals(value)) {
            return Status.NEW;
        } else if (Status.IN_PROGRESS.toString().equals(value)) {
            return Status.IN_PROGRESS;
        } else {
            return Status.DONE;
        }
    }

    public FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file.toPath());
        ArrayList<Integer> idList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Task task;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                } else {
                    task = fromString(line);
                    if (task instanceof Epic) {
                        Epic epic = (Epic) task;
                        idList.add(epic.getId());
                        epicMap.put(epic.getId(), epic);
                    } else if (task instanceof Subtask) {
                        Subtask subtask = (Subtask) task;
                        idList.add(subtask.getId());
                        subtaskMap.put(subtask.getId(), subtask);
                        epicMap.get(subtask.getEpicID()).getEpicSubtaskMap().put(subtask.getId(), subtask);
                        epicMap.get(subtask.getEpicID()).checkTime();
                        epicMap.get(subtask.getEpicID()).checkStatus();
                    } else {
                        idList.add(task.getId());
                        taskMap.put(task.getId(), task);
                    }
                }
            }
            int max = -1;
            for (Integer i : idList) {
                if (i > max) {
                    max = i;
                }
            }
            nextId = max + 1;
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при загрузке");
        }
        return manager;
    }

    public List<Task> getPrioritizedTasks() {
        return taskMap.values().stream()
                .filter(task -> task.getStartTime() != null) // Игнорируем задачи без времени начала
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEpicMap() {
        super.deleteEpicMap();
        save();
    }

    @Override
    public void deleteTaskMap() {
        super.deleteTaskMap();
        save();
    }

    @Override
    public void deleteSubtaskMap() {
        super.deleteSubtaskMap();
        save();
    }

    @Override
    public void addTask(Task task) {
            super.addTask(task);
            save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
            super.addSubtask(subtask);
            save();
    }

    @Override
    public void updateTask(Task task) {
           super.updateTask(task);
            save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
            super.updateSubtask(subtask);
            save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }
}
