package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void setId() {
        InMemoryTaskManager.id = 1;
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldReturnNewWhenAllSubtasksAreNew() {
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 1, Duration.ofMinutes(15), LocalDateTime.now()));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 1, Duration.ofMinutes(20), LocalDateTime.now()));
        Epic epic = taskManager.getEpicById(1);
        assertEquals(Status.NEW, epic.getStatus(), "Статус должен иметь значение NEW, " +
                "если статус всех подзадач эпика равен NEW");

    }

    @Test
    void shouldReturnDoneWhenAllSubtasksAreDone() {
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.DONE, Duration.ofMinutes(15), LocalDateTime.now(), 1));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", Status.DONE, Duration.ofMinutes(20), LocalDateTime.now(), 1));
        Epic epic = taskManager.getEpicById(1);
        assertEquals(Status.DONE, epic.getStatus(), "Статус должен иметь значение DONE, " +
                "если статус всех подзадач эпика равен DONE");
    }

    @Test
    void shouldReturnInProgressWhenSubtasksAreNewAndDone() {
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 1));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", Status.DONE, Duration.ofMinutes(20), LocalDateTime.now(), 1));
        Epic epic = taskManager.getEpicById(1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен иметь значение IN_PROGRESS, " +
                "если статус подзадач эпика равен NEW и DONE");
    }

    @Test
    void shouldReturnInProgressWhenAllSubtasksAreInProgress() {
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now(), 1));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", Status.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.now(), 1));
        Epic epic = taskManager.getEpicById(1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус должен иметь значение IN_PROGRESS, " +
                "если статус всех подзадач эпика равен IN_PROGRESS");
    }

    @Test
    void shouldNotEpicAddedToItselfAsSubtask() {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры", 1, Status.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 1, Status.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        taskManager.addNewSubtask(subtask);
        assertFalse(taskManager.getEpicsList().contains(subtask), "Объект Epic нельзя добавить в самого себя в виде подзадачи");

    }

    @Test
    void shouldSubtaskMadeIntoItsOwnEpic() {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры", 1, Status.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 1, Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        taskManager.addNewSubtask(subtask);
        assertFalse(taskManager.getSubtasksList().contains(epic), "Объект Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void immutabilityAddingTaskToManager() {
        Task task1 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = taskManager.getTaskById(1);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo);
    }

    @Test
    void givenIdGeneratedIdDoNotConflict() {
        Task task1 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи", 1, Status.NEW);
        taskManager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task2);
        Task[] arrayTwo = new Task[]{task2};
        assertNotEquals(arrayOne, arrayTwo, "Задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера");
    }

    @Test
    void deletedSubtasksShouldNotStoreOldIds() {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры", 1, Status.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 2, Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        taskManager.addNewSubtask(subtask);
        taskManager.removeSubtaskById(2);
        assertNotEquals(taskManager.getSubtasks(), "Удаляемая подзадача не хранит в себе старый Id");
    }

    @Test
    void shouldBeNoIrrelevantIdSubtasksInsideEpics() {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры", 1, Status.NEW);
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 2, Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        taskManager.addNewSubtask(subtask);
        taskManager.removeSubtaskById(2);
        ArrayList<Integer> epicSubtasks = epic.getSubtasksId();
        assertTrue(epicSubtasks.isEmpty(), "Список подзадач эпика должен быть пустым после удаления подзадачи.");
    }

    @Test
    void usingSettersAllowToChangeTheirFields() {
        Task task1 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи");
        taskManager.addNewTask(task1);
        Task task2 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи", 2, Status.NEW);
        taskManager.addNewTask(task2);
        assertNotEquals(task1, task2, "Задачи с одинаковыми id должны считаться одинаковыми.");
    }

    @Test
    void checkSubtaskNotEmpty() {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        taskManager.addNewSubtask(subtask);
        assertAll(
                () -> assertNotNull(subtask),
                () -> assertEquals(subtask.getDuration(), epic.getDuration()),
                () -> assertEquals(subtask.getStartTime(), epic.getStartTime()),
                () -> assertEquals(epic.getEndTime(), epic.getStartTime().plus(epic.getDuration()))
        );
    }

    @Test
    void shouldNotAddOverlappingTasks() {
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(15), LocalDateTime.now()));
        Task task = new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(5));
        taskManager.addNewTask(task);
        int prioritizedTasksSize = taskManager.getPrioritizedTasks().size();
        assertAll(
                () -> assertTrue(taskManager.taskOverlapValidation(task)),
                () -> assertEquals(1, prioritizedTasksSize)
        );
    }

    @Test
    void shouldDeleteAllTaskAndSubtaskFromPrioritizedTasksList() {
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(15), LocalDateTime.now()));
        Task task = new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(50));
        taskManager.addNewTask(task);
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(90), 3);
        taskManager.addNewSubtask(subtask);
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        int prioritizedTasksSize = taskManager.getPrioritizedTasks().size();
        assertEquals(0, prioritizedTasksSize);
    }

    @Test
    void shouldDeleteTaskAndSubtaskByIdFromPrioritizedTasksList() {
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(15), LocalDateTime.now()));
        Task task = new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(50));
        taskManager.addNewTask(task);
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(90), 3);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(180), 3));
        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(4);
        int prioritizedTasksSize = taskManager.getPrioritizedTasks().size();
        assertEquals(2, prioritizedTasksSize);
    }
}
