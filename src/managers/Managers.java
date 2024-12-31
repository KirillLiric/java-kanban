package managers;

import java.nio.file.Path;

public class  Managers {

    public static FileBackedTaskManager getDefault(Path fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
