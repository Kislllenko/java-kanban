package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> newTask = new HashMap<>();
    private HashMap<Integer, Epic> newEpic = new HashMap<>();
    private HashMap<Integer, Subtask> newSubtask = new HashMap<>();
    public ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
    public static Integer id = 1;

    public static Integer idCounter() {
        return id++;
    }

    // Получение списка всех задач
    public ArrayList<String> getTaskList() {
        ArrayList<String> allTasks = new ArrayList<>();

        for (Integer id : newTask.keySet()) {
            allTasks.add(newTask.get(id).toString());
        }

        System.out.println(allTasks);
        return allTasks;
    }

    public ArrayList<String> getEpicList() {
        ArrayList<String> allEpics = new ArrayList<>();

        for (Integer id : newEpic.keySet()) {
            allEpics.add(newEpic.get(id).toString());
        }

        System.out.println(allEpics);
        return allEpics;
    }

    public ArrayList<String> getSubtaskList() {
        ArrayList<String> allSubtasks = new ArrayList<>();

        for (Integer id : newSubtask.keySet()) {
            allSubtasks.add(newSubtask.get(id).toString());
        }

        System.out.println(allSubtasks);
        return allSubtasks;
    }

    // Удаление всех задач
    public void removeAllTasks() {
        newTask = new HashMap<>();
    }

    public void removeAllEpics() {
        newEpic = new HashMap<>();
    }

    public void removeAllSubtasks() {
        for (Epic epic : newEpic.values()) {
            epic.removeAllSubtasks();
        }
        newSubtask = new HashMap<>();
    }

    // Получение по идентификатору
    public Task getTaskById(Integer id) {
        Task task = newTask.get(id);
        System.out.println(task);
        return task;
    }

    public Epic getEpicById(Integer id) {
        Epic epic = newEpic.get(id);
        System.out.println(epic);
        return epic;
    }

    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = newSubtask.get(id);
        System.out.println(subtask);
        return subtask;
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    public void addNewTask(Task task) {
        newTask.put(id, task);
        idCounter();
    }

    // Создание Эпика. Сам объект должен передаваться в качестве параметра.
    public void addNewEpic(Epic epic) {
        newEpic.put(id, epic);
        idCounter();
    }

    // Создание Подзадачи. Сам объект должен передаваться в качестве параметра.
    public ArrayList<Subtask> addNewSubtask(Subtask subtask) {
        newSubtask.put(id, subtask);
        Epic epic = newEpic.get(subtask.getEpicId());
        epic.addSubtaskId(id);
        idCounter();
        return subtaskArrayList;

    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Integer id, Task task) {
        task.setId(id);
        newTask.put(id, task);

    }

    public void updateEpic(Integer id, Epic epic) {
        Epic oldEpic = newEpic.get(id);
        ArrayList<Integer> subtaskId = oldEpic.getSubtaskId();
        epic.setId(id);
        epic.setSubtaskId(subtaskId);
        newEpic.put(id, epic);

    }

    public void updateSubtask(Integer id, Subtask subtask) {
        subtask.setId(id);
        newSubtask.put(id, subtask);

        Epic epic = newEpic.get(subtask.getEpicId());
        ArrayList<Integer> allSubtasksIds = epic.getSubtaskId();

        boolean areAllSubtasksNew = true;
        boolean areAllSubtasksDone = true;

        for (Integer subtaskId : allSubtasksIds) {
            Subtask tmpSubtask = newSubtask.get(subtaskId);
            if (tmpSubtask.getStatus() != Status.NEW) {
                areAllSubtasksNew = false;
            }
            if (tmpSubtask.getStatus() != Status.DONE) {
                areAllSubtasksDone = false;
            }

        }

        if (areAllSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (areAllSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    // Удаление по идентификатору.
    public void removeTaskById(Integer id) {
        newTask.remove(id);

    }

    public void removeEpicById(Integer id) {
        newEpic.remove(id);

    }

    public void removeSubtaskById(Integer id) {
        for (Epic epic : newEpic.values()) {
            if (epic.getSubtaskId().contains(id)) {
                epic.removeSubtaskId(id);
            }
        }
        newSubtask.remove(id);

    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<String> getAllSubtaskByEpicId(Integer epicId) {

        Epic epic = newEpic.get(epicId);
        ArrayList<Integer> subtasksId = epic.getSubtaskId();
        ArrayList<String> subtasksByEpicId = new ArrayList<>();

        for (Integer id : subtasksId) {
            subtasksByEpicId.add(newSubtask.get(id).toString());
        }

        System.out.println(subtasksByEpicId);
        return subtasksByEpicId;
    }

}
