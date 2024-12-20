package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager fileBackedTaskManager;
    File tempFile;

    @BeforeEach
    public void createFileAndSetId() throws IOException {
        tempFile = File.createTempFile("tmpBackupFile", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        FileBackedTaskManager.id = 1;

    }

    @Test
    public void shouldLoadEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertAll(
                () -> assertEquals(0, fileBackedTaskManager.getTasksList().size(), "В списке есть задачи"),
                () -> assertEquals(0, fileBackedTaskManager.getEpicsList().size(), "В списке есть задачи"),
                () -> assertEquals(0, fileBackedTaskManager.getSubtasksList().size(), "В списке есть задачи")
        );
    }

    @Test
    public void shouldSaveEmptyFile() {
        try {
            fileBackedTaskManager.save();
            FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
            List<String> taskList = fileBackedTaskManager.getTasksList();
            assertTrue(taskList.isEmpty(), "Список содержит задачи");
        } catch (ManagerSaveException exception) {
            assertEquals("Ошибка сохранения данных в файл", exception.getMessage());
        }
    }

    @Test
    public void shouldSaveTasksInFile() {
        fileBackedTaskManager.addNewTask(new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addNewTask(new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт Дизайн квартиры"));
        fileBackedTaskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы 3D визуализация Смета", 3, Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов Покраска стен Установка кухни", 3, Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addNewEpic(new Epic("Путешествие", "План отдыха"));
        fileBackedTaskManager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу Пляжный отдых Подняться на гору", 6, Duration.ofMinutes(15), LocalDateTime.now()));
        int i = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(tempFile))) {
            while (fileReader.ready()) {
                i++;
                fileReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(8, i);
    }

    @Test
    public void shouldLoadTasksFromFile() {
        fileBackedTaskManager.addNewTask(new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(15), LocalDateTime.now()));
        fileBackedTaskManager.addNewTask(new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(25), LocalDateTime.now()));
        fileBackedTaskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт Дизайн квартиры"));
        fileBackedTaskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы 3D визуализация Смета", 3, Duration.ofMinutes(25), LocalDateTime.now()));
        fileBackedTaskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов Покраска стен Установка кухни", 3, Duration.ofMinutes(25), LocalDateTime.now()));
        FileBackedTaskManager fileBackedTaskManagerLoadFromFile = FileBackedTaskManager.loadFromFile(tempFile);
        assertAll(
                () -> assertEquals(fileBackedTaskManager.getTasksList(), fileBackedTaskManagerLoadFromFile.getTasksList()),
                () -> assertEquals(fileBackedTaskManager.getEpicsList(), fileBackedTaskManagerLoadFromFile.getEpicsList()),
                () -> assertEquals(fileBackedTaskManager.getSubtasksList(), fileBackedTaskManagerLoadFromFile.getSubtasksList())
        );
    }

    @Test
    void shouldThrowManagerSaveExceptionWhenFileCannotBeRead() {
        File invalidFile = new File("/invalid_path/invalidFile.csv");
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(invalidFile);
        }, "Должно выбрасываться исключение класса ManagerSaveException");
    }

}
