package managers;

import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager(Path fileName) {
        return new FileBackedTaskManager(fileName);
    }
}
