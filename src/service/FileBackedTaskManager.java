package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Integer id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(Integer id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubtask(Integer id, Subtask subtask) {
        super.updateSubtask(id, subtask);
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    public void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fileWriter.append(HEADER);
            fileWriter.newLine();
            for (String task : getTasksList()) {
                fileWriter.write(task);
                fileWriter.newLine();
            }
            for (String epic : getEpicsList()) {
                fileWriter.write(epic);
                fileWriter.newLine();
            }
            for (String subtask : getSubtasksList()) {
                fileWriter.write(subtask);
                fileWriter.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

    }

    private static Task fromString(String value) {
        String[] dataArray = value.split(",");
        int id = Integer.parseInt(dataArray[0]);
        TaskTypes type = TaskTypes.valueOf(dataArray[1]);
        String name = dataArray[2];
        Status status = Status.valueOf(dataArray[3]);
        String description = dataArray[4];

        switch (type) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status);
            case SUBTASK:
                return new Subtask(name, description, id, status, Integer.parseInt(dataArray[5]));
            default:
                throw new IllegalStateException("Неожиданное значение: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        if (file.isDirectory()) {
            throw new ManagerSaveException("Вместо файла указана директория.");
        } else if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException exception) {
                throw new ManagerSaveException("Файл не найден. Ошибка при попытке создать новый файл.");
            }
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.equals(HEADER)) {
                    continue;
                }

                Task task = fromString(line);

                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.newTasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        if (task instanceof Epic) {
                            fileBackedTaskManager.newEpics.put(task.getId(), (Epic) task);
                            break;
                        }
                    case SUBTASK:
                        if (task instanceof Subtask) {
                            fileBackedTaskManager.newSubtasks.put(task.getId(), (Subtask) task);
                            break;
                        }
                    default:
                        throw new IllegalStateException("Unexpected value: " + task.getType());
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

        return fileBackedTaskManager;
    }
}
