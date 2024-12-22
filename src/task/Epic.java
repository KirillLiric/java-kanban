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
        if ( !(startTime == null || duration == null) ) {
            return super.id + "," + Tasks.EPIC + "," + super.name + "," + super.status + "," + super.description + ","
                    + startTime + "," + duration.toMinutes();
        } else {
            return super.id + "," + Tasks.EPIC + "," + super.name + "," + super.status + "," + super.description;
        }
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
        return Duration.between(getStartTime(), getEndTime());
    }

    @Override
    public LocalDateTime getEndTime() {
        Optional<LocalDateTime> maxEndTime = epicSubtaskMap.values().stream()
                .map(Subtask::getEndTime)
                .filter(endTime -> endTime != null)
                .max(LocalDateTime::compareTo);
        return maxEndTime.orElse(null);
    }

    public void checkTime() {
        startTime = getStartTime();
        duration = getDuration();
    }

    public void checkStatus() {

        if (epicSubtaskMap.isEmpty()) {
            super.setStatus(Status.NEW);
        }
        boolean hasInProgress =
                ((epicSubtaskMap.values().stream()
                        .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS)) ||
                        (epicSubtaskMap.values().stream()
                                .anyMatch(subtask -> subtask.getStatus() == Status.DONE) &&
                                (epicSubtaskMap.values().stream()
                                        .anyMatch(subtask -> subtask.getStatus() == Status.NEW))));

        boolean hasDone = epicSubtaskMap.values().stream()
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        if (hasInProgress) {
            super.setStatus(Status.IN_PROGRESS);
        } else if (hasDone) {
            super.setStatus(Status.DONE);
        } else {
            super.setStatus(Status.NEW);
        }
    }

}
