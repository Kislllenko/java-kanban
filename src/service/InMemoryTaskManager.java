package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> newTasks = new HashMap<>();
    protected HashMap<Integer, Epic> newEpics = new HashMap<>();
    protected HashMap<Integer, Subtask> newSubtasks = new HashMap<>();
    public static Integer id = 1;

    public static Integer idCounter() {
        return id++;
    }

    // Получение списка всех задач
    @Override
    public ArrayList<String> getTasksList() {
        ArrayList<String> allTasks = new ArrayList<>();

        for (Integer id : newTasks.keySet()) {
            allTasks.add(newTasks.get(id).toString());
        }

        return allTasks;
    }

    @Override
    public ArrayList<String> getEpicsList() {
        ArrayList<String> allEpics = new ArrayList<>();

        for (Integer id : newEpics.keySet()) {
            allEpics.add(newEpics.get(id).toString());
        }

        return allEpics;
    }

    @Override
    public ArrayList<String> getSubtasksList() {
        ArrayList<String> allSubtasks = new ArrayList<>();

        for (Integer id : newSubtasks.keySet()) {
            allSubtasks.add(newSubtasks.get(id).toString());
        }

        return allSubtasks;
    }

    // Удаление всех задач
    @Override
    public void removeAllTasks() {
        newTasks = new HashMap<>();
    }

    @Override
    public void removeAllEpics() {
        newEpics = new HashMap<>();
        newSubtasks = new HashMap<>();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : newEpics.values()) {
            epic.removeAllSubtasks();
        }
        newSubtasks = new HashMap<>();
    }

    // Получение по идентификатору
    @Override
    public Task getTaskById(Integer id) {
        Task task = newTasks.get(id);

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = newEpics.get(id);

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        if (epic != null) {
            historyManager.add(epic);
        }

        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = newSubtasks.get(id);

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        if (subtask != null) {
            historyManager.add(subtask);
        }

        return subtask;
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewTask(Task task) {
        newTasks.put(id, task);
        idCounter();
    }

    // Создание Эпика. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewEpic(Epic epic) {
        newEpics.put(id, epic);
        idCounter();
    }

    // Создание Подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addNewSubtask(Subtask subtask) {
        newSubtasks.put(id, subtask);
        Epic epic = newEpics.get(subtask.getEpicId());
        epic.addSubtaskId(id);
        idCounter();
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Integer id, Task task) {
        task.setId(id);
        newTasks.put(id, task);

    }

    @Override
    public void updateEpic(Integer id, Epic epic) {
        Epic oldEpic = newEpics.get(id);
        ArrayList<Integer> subtasksId = oldEpic.getSubtasksId();
        epic.setId(id);
        epic.setSubtaskId(subtasksId);
        newEpics.put(id, epic);

    }

    @Override
    public void updateSubtask(Integer id, Subtask subtask) {
        subtask.setId(id);
        newSubtasks.put(id, subtask);

        Epic epic = newEpics.get(subtask.getEpicId());
        ArrayList<Integer> allSubtasksIds = epic.getSubtasksId();

        boolean areAllSubtasksNew = true;
        boolean areAllSubtasksDone = true;

        for (Integer subtasksId : allSubtasksIds) {
            Subtask tmpSubtask = newSubtasks.get(subtasksId);
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
    @Override
    public void removeTaskById(Integer id) {
        newTasks.remove(id);

    }

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = newEpics.get(id);
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        for (Integer subtask : subtasksId) {
            newSubtasks.remove(subtask);
        }

        newEpics.remove(id);

    }

    @Override
    public void removeSubtaskById(Integer id) {
        for (Epic epic : newEpics.values()) {
            if (epic.getSubtasksId().contains(id)) {
                epic.removeSubtaskId(id);
            }
        }
        newSubtasks.remove(id);

    }

    // Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<String> getAllSubtasksByEpicId(Integer epicId) {

        Epic epic = newEpics.get(epicId);
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        ArrayList<String> subtasksByEpicId = new ArrayList<>();

        for (Integer id : subtasksId) {
            subtasksByEpicId.add(newSubtasks.get(id).toString());
        }

        return subtasksByEpicId;
    }

}
