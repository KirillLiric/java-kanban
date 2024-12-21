package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task {

    LocalDateTime startTime;
    Duration duration;

    private HashMap<Integer, Subtask> epicSubtaskMap;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtaskMap = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getEpicSubtaskMap() {
        return epicSubtaskMap;
    }

    @Override
    public String toString() {
        return super.id + "," + Tasks.EPIC + "," + super.name + "," + super.status + "," + super.description + ","
                + startTime + "," + duration.toMinutes();
    }

    @Override
    public LocalDateTime getStartTime() {
        Optional<LocalDateTime> minStartTime = epicSubtaskMap.values().stream()
                .map(Subtask::getStartTime)
                .filter(startTime -> startTime != null)
                .min(LocalDateTime::compareTo);
        return minStartTime.orElse(null);
    }

    @Override
    public Duration getDuration() {
        return epicSubtaskMap.values().stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime startTime = getStartTime();
        if (startTime != null) {
            return startTime.plus(getDuration());
        }
        return null;
    }

    public void checkTime() {
        startTime = getStartTime();
        duration = getDuration();
    }

}
