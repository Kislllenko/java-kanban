package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

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

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description=" + super.getDescription() +
                ", status=" + super.getStatus() +
                ", id=" + super.getId() +
                ", subtasksId=" + subtasksId +
                '}';
    }
}
