package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(String data) {
        this.startTime = LocalDateTime.parse(data);
    }

    public void setDuration(String data) {
        this.duration = Duration.ofMinutes(Integer.parseInt(data));
    }

    @Override
    public boolean equals(Object o) {
        Task obj = (Task) o;
        if (this.id == (obj.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id * 17;
    }

    @Override
    public String toString() {
        return id + "," + Tasks.TASK + "," + name + "," + status + "," + description + "," + startTime + ","
                + duration.toMinutes();
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

}
