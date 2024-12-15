package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {

    TaskManager taskManager;
    HistoryManager historyTaskManager;

    @BeforeEach
    public void setId() {
        InMemoryTaskManager.id = 1;
        taskManager = Managers.getDefault();
        historyTaskManager = Managers.getDefaultHistory();
    }

    @AfterEach
    public void removeHistory() {
        List<Task> tasks = historyTaskManager.getHistory();
        for (Task task : tasks) {
            historyTaskManager.remove(task.getId());
        }
    }

    @Test
    void shouldAddHistoryInOrder() {
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи"));
        taskManager.addNewTask(new Task("Покупки", "Хлеб, Молоко, Корм для щенка"));
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 3));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3));
        taskManager.addNewEpic(new Epic("Путешествие", "План отдыха"));
        taskManager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу, Пляжный отдых, Подняться на гору", 6));
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(6);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(7);
        List<Task> history = historyTaskManager.getHistory();
        int expectedListSize = 7;
        int listSize = history.size();
        assertAll(
                () -> assertEquals(expectedListSize, listSize,
                        "Фактическое кол-во просмотров не соответствует ожидаемому"),
                () -> assertTrue(history.toString().startsWith("[1,TASK,Переезд,NEW,Собрать коробки, Упаковать цветы, Передать ключи,null,null"))
        );
    }

    @Test
    void shouldRemoveDoubleTask() {
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи"));
        taskManager.addNewTask(new Task("Покупки", "Хлеб, Молоко, Корм для щенка"));
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 3));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3));
        taskManager.addNewEpic(new Epic("Путешествие", "План отдыха"));
        taskManager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу, Пляжный отдых, Подняться на гору", 6));
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(6);
        taskManager.getEpicById(6);
        taskManager.getSubtaskById(7);
        taskManager.getSubtaskById(7);
        List<Task> history = historyTaskManager.getHistory();
        int expectedListSize = 7;
        int listSize = history.size();
        assertAll(
                () -> assertEquals(expectedListSize, listSize,
                        "Фактическое кол-во просмотров не соответствует ожидаемому"),
                () -> assertTrue(history.toString().startsWith("[1,TASK,Переезд,NEW,Собрать коробки, Упаковать цветы, Передать ключи,null,null"))
        );
    }

    @Test
    void removeMethodRemovesTaskFromHistory() {
        Task task = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task);
        historyTaskManager.add(task);
        historyTaskManager.remove(task.getId());
        assertEquals(historyTaskManager.getHistory().size(), 0);
    }

    @Test
    void addMethodAddsTaskFromHistory() {
        Task task = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task);
        historyTaskManager.add(task);
        assertEquals(historyTaskManager.getHistory().size(), 1);
    }

    @Test
    void whenCreatingTheHistoryIsEmpty() {
        assertEquals(historyTaskManager.getHistory().size(), 0);
    }

    @Test
    void whileTheTasksAreNotViewedTheHistoryIsEmpty() {
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        assertEquals(historyTaskManager.getHistory().size(), 0);
    }

    @Test
    void deletingFromTheBeginningOfTheHistory() {
        Task task = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task);
        Task task1 = new Task("Покупки", "Хлеб, Молоко, Корм для щенка");
        taskManager.addNewTask(task1);
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewTask(epic);
        historyTaskManager.add(task);
        historyTaskManager.add(task1);
        historyTaskManager.add(epic);
        historyTaskManager.remove(task.getId());
        assertAll(
                () -> assertEquals(2, historyTaskManager.getHistory().size()),
                () -> assertEquals(task1, historyTaskManager.getHistory().get(0)),
                () -> assertEquals(epic, historyTaskManager.getHistory().get(1))
        );
    }

    @Test
    void deletingFromTheMidOfTheHistory() {
        Task task = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task);
        Task task1 = new Task("Покупки", "Хлеб, Молоко, Корм для щенка");
        taskManager.addNewTask(task1);
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewTask(epic);
        historyTaskManager.add(task);
        historyTaskManager.add(task1);
        historyTaskManager.add(epic);
        historyTaskManager.remove(task1.getId());
        assertAll(
                () -> assertEquals(2, historyTaskManager.getHistory().size()),
                () -> assertEquals(task, historyTaskManager.getHistory().get(0)),
                () -> assertEquals(epic, historyTaskManager.getHistory().get(1))
        );
    }

    @Test
    void deletingFromTheEndOfTheHistory() {
        Task task = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task);
        Task task1 = new Task("Покупки", "Хлеб, Молоко, Корм для щенка");
        taskManager.addNewTask(task1);
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewTask(epic);
        historyTaskManager.add(task);
        historyTaskManager.add(task1);
        historyTaskManager.add(epic);
        historyTaskManager.remove(epic.getId());
        assertAll(
                () -> assertEquals(2, historyTaskManager.getHistory().size()),
                () -> assertEquals(task, historyTaskManager.getHistory().get(0)),
                () -> assertEquals(task1, historyTaskManager.getHistory().get(1))
        );
    }
}
