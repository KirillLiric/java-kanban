package managers;

import task.*;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private Path saveFile;

    FileBackedTaskManager(String saveFile) {
        this.saveFile = Paths.get(saveFile);
    }

    public void save() {
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
            throw new ManagerSaveException();
        }
    }

    public static Task fromString(String value) {
        String[] taskText = value.split(",");
        Task task;
        if(Tasks.TASK.toString().equals(taskText[1])) {
            task = new Task(taskText[2], taskText[4]);
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
        } else if (Tasks.EPIC.toString().equals(taskText[1])) {
            task = new Epic(taskText[2], taskText[4]);
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
        } else if (Tasks.SUBTASK.toString().equals(taskText[1])) {
            task = new Subtask(taskText[2], taskText[4], Integer.parseInt(taskText[5]));
            task.setId(Integer.parseInt(taskText[0]));
            task.setStatus(getStatus(taskText[3]));
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                } else {
                    task = fromString(line);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        } catch (NumberFormatException e) {
            throw new ManagerSaveException();
        }

        return manager;
    }



    //Удаление всех задач

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

    //Обновление

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

    //Удаление по Id

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
