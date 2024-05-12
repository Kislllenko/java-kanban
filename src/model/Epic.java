package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public ArrayList<Integer> removeSubtaskId(Integer id) {
        subtaskId.remove(id);
        return subtaskId;
    }

    public ArrayList<Integer> removeAllSubtasks() {
        subtaskId = new ArrayList<>();
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    public void addSubtaskId(Integer id) {
        subtaskId.add(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + super.getName() + '\'' +
                ", description=" + super.getDescription() +
                ", status=" + super.getStatus() +
                ", id=" + super.getId() +
                ", subtaskId=" + subtaskId +
                '}';
    }
}
