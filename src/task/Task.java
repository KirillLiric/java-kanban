package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {

    public String name;
    public String description;
    public Integer id;
    public Status status;
    public Duration duration;
    public LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, Status status, Duration duration,
                LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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
