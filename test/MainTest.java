import managers.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void epicStatus() throws IOException {
        File tempFile = File.createTempFile("tempFile1", ".txt");
        String testText = "1,EPIC,Epic1,NEW,Description epic1,\n" +
                "2,SUBTASK,Sub Task1,NEW,Description sub task1,1,2024-12-19T12:56:01.918810,15\n" +
                "3,SUBTASK,Sub Task2,NEW,Description sub task2,1,2024-12-20T12:56:01.918810,15\n" +
                "4,EPIC,Epic2,NEW,Description epic2,\n" +
                "5,SUBTASK,Sub Task3,DONE,Description sub task3,4,2024-12-21T12:56:01.918810,15\n" +
                "6,SUBTASK,Sub Task4,DONE,Description sub task4,4,2024-12-22T12:56:01.918810,15\n" +
                "7,EPIC,Epic3,NEW,Description epic3,\n" +
                "8,SUBTASK,Sub Task5,NEW,Description sub task5,7,2024-12-23T12:56:01.918810,15\n" +
                "9,SUBTASK,Sub Task6,DONE,Description sub task6,7,2024-12-24T12:56:01.918810,15\n" +
                "10,EPIC,Epic4,NEW,Description epic4,\n" +
                "11,SUBTASK,Sub Task7,IN_PROGRESS,Description sub task7,10,2024-12-25T12:56:01.918810,15\n" +
                "12,SUBTASK,Sub Task8,IN_PROGRESS,Description sub task8,10,2024-12-26T12:56:01.918810,15\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(testText);
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileBackedTaskManager manager = Managers.getDefaultFileBackedTaskManager(tempFile.toPath());
        manager.loadFromFile(tempFile);


        assertEquals(Status.NEW, manager.getEpicMap().get(1).getStatus(), "Провалена проверка пункта a");
        assertEquals(Status.DONE, manager.getEpicMap().get(4).getStatus(), "Провалена проверка пункта b");
        assertEquals(Status.IN_PROGRESS, manager.getEpicMap().get(7).getStatus(), "Провалена проверка пункта c");
        assertEquals(Status.IN_PROGRESS, manager.getEpicMap().get(10).getStatus(), "Провалена проверка пункта d");
    }

}






