import java.util.HashMap;
public class Epic extends Task{

    private HashMap<Integer, Subtask> subtaskMap;

    public Epic(String name, String description, HashMap<Integer, Subtask> subtaskMap) {
        super(name, description);
        this.subtaskMap = subtaskMap;
    }

    public HashMap<Integer, Subtask> getSubtaskMap() {
        return subtaskMap;
    }

}
