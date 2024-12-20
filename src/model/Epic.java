package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
    }

    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public ArrayList<Integer> removeSubtaskId(Integer id) {
        subtasksId.remove(id);
        return subtasksId;
    }

    public ArrayList<Integer> removeAllSubtasks() {
        subtasksId = new ArrayList<>();
        return subtasksId;
    }

    public void setSubtaskId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void addSubtaskId(Integer id) {
        subtasksId.add(id);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getId() + ","
                + TaskTypes.EPIC + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + getDuration() + ","
                + getStartTime();
    }
}
