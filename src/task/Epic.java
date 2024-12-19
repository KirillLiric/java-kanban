package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task {

    LocalDateTime startTime = getStartTime();
    Duration duration = getDuration();

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
                + startTime + "," + duration;
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
        Optional<LocalDateTime> maxTime = epicSubtaskMap.values().stream()
                .map(Subtask::getStartTime)
                .filter(startTime -> startTime != null)
                .max(LocalDateTime::compareTo);
        return maxTime.orElse(null);
    }

}
