package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Получение списка всех задач
    ArrayList<String> getTasksList();

    ArrayList<String> getEpicsList();

    ArrayList<String> getSubtasksList();

    // Удаление всех задач
    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    // Получение по идентификатору
    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    void addNewTask(Task task);

    // Создание Эпика. Сам объект должен передаваться в качестве параметра.
    void addNewEpic(Epic epic);

    // Создание Подзадачи. Сам объект должен передаваться в качестве параметра.
    void addNewSubtask(Subtask subtask);

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Integer id, Task task);

    void updateEpic(Integer id, Epic epic);

    void updateSubtask(Integer id, Subtask subtask);

    // Удаление по идентификатору.
    void removeTaskById(Integer id);

    void removeEpicById(Integer id);

    void removeSubtaskById(Integer id);

    // Получение списка всех подзадач определённого эпика.
    ArrayList<String> getAllSubtasksByEpicId(Integer epicId);
}
