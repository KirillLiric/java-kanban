package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Duration duration, LocalDateTime time,
                   int epicID) {
        super(name, description, status, duration, time);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public void setNullEpicId() {
        this.epicID = -1;
    }

    @Override
    public String toString() {
        return super.id + "," + Tasks.SUBTASK + "," + super.name + "," + super.status + "," + super.description + ","
                + getEpicID() + "," + super.startTime + "," + super.duration.toMinutes();
    }
}
