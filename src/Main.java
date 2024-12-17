import managers.FileBackedTaskManager;
import managers.Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("tempFile1", ".txt");
        Path path = Paths.get("C:\\Users\\User\\Desktop\\Test\\Test.txt");

        //Запись в файл тестового текста
        String testText = "1,TASK,Task1,NEW,Description task1,\n2,EPIC,Epic2,DONE,Description epic2,\n" +
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write(testText);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager manager = Managers.getDefault(path);
        manager.loadFromFile(path.toFile());
        System.out.println(manager.getTaskMap());
        System.out.println(manager.getEpicMap());
        System.out.println(manager.getSubtaskMap());


    }
}

