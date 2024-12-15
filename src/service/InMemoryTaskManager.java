package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> newTasks = new HashMap<>();
    protected HashMap<Integer, Epic> newEpics = new HashMap<>();
    protected HashMap<Integer, Subtask> newSubtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>();
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
        for (Task task : newTasks.values()) {
            prioritizedTasks.remove(task);
        }
        newTasks = new HashMap<>();
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks();
        newEpics = new HashMap<>();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : newEpics.values()) {
            epic.removeAllSubtasks();
        }
        for (Subtask subtasks : newSubtasks.values()) {
            prioritizedTasks.remove(subtasks);
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
        if (taskOverlapValidation(task)) {
            task.setId(task.getId());
            newTasks.put(id, task);
            idCounter();
        } else {
            task.setId(task.getId());
            newTasks.put(id, task);
            updatePrioritizedTasks();
            idCounter();
        }
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
        if (taskOverlapValidation(subtask)) {
            newSubtasks.put(id, subtask);
            Epic epic = newEpics.get(subtask.getEpicId());
            ArrayList<Integer> allSubtasksIds = epic.getSubtasksId();
            epic.addSubtaskId(id);
            epic.setDuration(getEpicDuration(epic));
            epic.setStartTime(getEpicStartTime(epic));
            epic.setEndTime(getEpicEndTime(epic));
            idCounter();

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
            updatePrioritizedTasks();
        } else {
            newSubtasks.put(id, subtask);
            Epic epic = newEpics.get(subtask.getEpicId());
            epic.addSubtaskId(id);
            epic.setDuration(getEpicDuration(epic));
            epic.setStartTime(getEpicStartTime(epic));
            epic.setEndTime(getEpicEndTime(epic));
            updatePrioritizedTasks();
            idCounter();
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
    }

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Integer id, Task task) {
        if (taskOverlapValidation(task)) {
            task.setId(id);
            newTasks.put(id, task);
        } else {
            task.setId(id);
            newTasks.put(id, task);
            updatePrioritizedTasks();
        }
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
        if (taskOverlapValidation(subtask)) {
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

        } else {
            subtask.setId(id);
            newSubtasks.put(id, subtask);
            updatePrioritizedTasks();
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
    }

    // Удаление по идентификатору.
    @Override
    public void removeTaskById(Integer id) {
        prioritizedTasks.remove(newTasks.remove(id));
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
        prioritizedTasks.remove(newSubtasks.remove(id));
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(newTasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(newSubtasks.values());
    }

    private LocalDateTime getEpicStartTime(Epic epic) {
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer id : subtasksId) {
            subtasks.add(getSubtaskById(id));
        }
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime getEpicEndTime(Epic epic) {
        return Optional.ofNullable(epic.getStartTime())
                .map(startTime -> startTime.plus(epic.getDuration()))
                .orElse(null);
    }

    private Duration getEpicDuration(Epic epic) {
        ArrayList<Integer> subtasksId = epic.getSubtasksId();
        List<Subtask> subtasks = new ArrayList<>();

        for (Integer id : subtasksId) {
            subtasks.add(getSubtaskById(id));
        }
        return subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        for (Task task : getTasks()) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
        for (Subtask subtask : getSubtasks()) {
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    public boolean taskOverlapValidation(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            LocalDateTime taskEndTime = task.getEndTime();
            LocalDateTime prioritizedTaskEndTime = prioritizedTask.getEndTime();
            if (taskEndTime.isAfter(prioritizedTask.getStartTime()) && task.getStartTime().isBefore(prioritizedTaskEndTime)) {
                return true;
            }
        }
        prioritizedTasks.add(task);
        return false;
    }
}
