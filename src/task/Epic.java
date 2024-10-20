package task;

import java.util.HashMap;

public class Epic extends Task {

    private HashMap<Integer, Subtask> epicSubtaskMap;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtaskMap = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getEpicSubtaskMap() {
        return epicSubtaskMap;
    }

}
