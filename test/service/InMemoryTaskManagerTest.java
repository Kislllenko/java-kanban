package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void createTasksAndSetId() {
        InMemoryTaskManager.id = 1;
        taskManager = Managers.getDefault();
        taskManager.addNewTask(new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи"));
        taskManager.addNewTask(new Task("Покупки", "Хлеб, Молоко, Корм для щенка"));
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры"));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 3));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3));
        taskManager.addNewEpic(new Epic("Путешествие", "План отдыха"));
        taskManager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу, Пляжный отдых, Подняться на гору", 6));
    }


    @Test
    @Order(1)
    void checkGetTaskById() {
        Task task = taskManager.getTaskById(1);
        assertEquals("1,TASK,Переезд,NEW,Собрать коробки, Упаковать цветы, Передать ключи,null,null",
                task.toString());
    }

    @Test
    @Order(2)
    void checkGetEpicById() {
        Epic epic = taskManager.getEpicById(3);
        assertEquals("3,EPIC,Ремонт квартиры,NEW,Ремонт, Дизайн квартиры,PT0S,null",
                epic.toString());
    }

    @Test
    @Order(3)
    void checkGetSubtaskById() {
        Subtask subtask = taskManager.getSubtaskById(4);
        assertEquals("4,SUBTASK,Дизайн квартиры,NEW,Референсы, 3D визуализация, Смета,null,null,3",
                subtask.toString());
    }

    @Test
    @Order(4)
    void checkGetTasksList() {
        ArrayList<String> tasks = taskManager.getTasksList();
        int amountOfTasks = tasks.size();
        assertEquals(2, amountOfTasks,
                "Фактическое кол-во задач не соответствует ожидаемому");
    }

    @Test
    @Order(5)
    void checkGetEpicsList() {
        ArrayList<String> epics = taskManager.getEpicsList();
        int amountOfEpics = epics.size();
        assertEquals(2, amountOfEpics,
                "Фактическое кол-во эпиков не соответствует ожидаемому");
    }

    @Test
    @Order(6)
    void checkGetSubtasksList() {
        ArrayList<String> subtasks = taskManager.getSubtasksList();
        int amountOfSubtasks = subtasks.size();
        assertEquals(3, amountOfSubtasks,
                "Фактическое кол-во подзадач не соответствует ожидаемому");
    }

    @Test
    @Order(7)
    void shouldTaskEqualIfIdEqual() {
        Task task = taskManager.getTaskById(2);
        taskManager.updateTask(2, new Task("Покупки", "Хлеб, Молоко, Корм для щенка", Status.NEW));
        Task updatedTask = taskManager.getTaskById(2);
        assertEquals(task, updatedTask, "Задачи не идентичны");
    }

    @Test
    @Order(8)
    void shouldTaskHeirsEpicEqualIfIdEqual() {
        Epic epic = taskManager.getEpicById(6);
        taskManager.updateEpic(6, new Epic("Путешествие", "План отдыха"));
        Epic updatedEpic = taskManager.getEpicById(6);
        assertEquals(epic, updatedEpic, "Эпики не идентичны");
    }

    @Test
    @Order(9)
    void shouldTaskHeirsSubtaskEqualIfIdEqual() {
        Subtask subtask = taskManager.getSubtaskById(5);
        taskManager.updateSubtask(5, new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3, Status.NEW));
        Subtask updatedSubtask = taskManager.getSubtaskById(5);
        assertEquals(subtask, updatedSubtask, "Подзадачи не идентичны");
    }

    @Test
    @Order(10)
    void checkUpdateTask() {
        Task task = taskManager.getTaskById(1);
        taskManager.updateTask(1, new Task("Переезд", "Собрать коробки, " +
                "Упаковать цветы, Передать ключи", Status.IN_PROGRESS));
        Task updatedTask = taskManager.getTaskById(1);
        assertNotEquals(task.toString(), updatedTask.toString(), "Задачи идентичны");
    }

    @Test
    @Order(11)
    void checkUpdateEpic() {
        Epic epic = taskManager.getEpicById(3);
        taskManager.updateEpic(3, new Epic("Ремонт", "Дизайн квартиры"));
        Epic updatedEpic = taskManager.getEpicById(3);
        assertNotEquals(epic.toString(), updatedEpic.toString(), "Задачи идентичны");
    }

    @Test
    @Order(12)
    void checkUpdateSubtask() {
        Subtask subtask = taskManager.getSubtaskById(4);
        taskManager.updateSubtask(4, new Subtask("Дизайн офиса",
                "Рефы, Визуализация, Стоимость работ", 3, Status.DONE));
        Subtask updatedSubtask = taskManager.getSubtaskById(4);
        assertNotEquals(subtask.toString(), updatedSubtask.toString(), "Задачи идентичны");
    }

    @Test
    @Order(13)
    void checkGetAllSubtasksByEpicId() {
        ArrayList<String> subtasks = taskManager.getAllSubtasksByEpicId(3);
        int amountOfSubtasks = subtasks.size();
        assertEquals(2, amountOfSubtasks,
                "Фактическое кол-во подзадач не соответствует ожидаемому");
    }

    @Test
    @Order(14)
    void checkRemoveTaskById() {
        taskManager.removeTaskById(1);
        Task task = taskManager.getTaskById(1);
        assertNull(task);
    }

    @Test
    @Order(15)
    void checkRemoveEpicById() {
        taskManager.removeEpicById(3);
        Epic epic = taskManager.getEpicById(3);
        assertNull(epic);
    }

    @Test
    @Order(16)
    void checkRemoveSubtaskById() {
        taskManager.removeSubtaskById(4);
        Subtask subtask = taskManager.getSubtaskById(4);
        assertNull(subtask);
    }

    @Test
    @Order(17)
    void checkRemoveAllTasks() {
        taskManager.removeAllTasks();
        ArrayList<String> tasks = taskManager.getTasksList();
        int amountOfTasks = tasks.size();
        assertEquals(0, amountOfTasks,
                "Фактическое кол-во задач не соответствует ожидаемому");
    }

    @Test
    @Order(18)
    void checkRemoveAllEpics() {
        taskManager.removeAllEpics();
        ArrayList<String> epics = taskManager.getEpicsList();
        int amountOfEpics = epics.size();
        assertEquals(0, amountOfEpics,
                "Фактическое кол-во эпиков не соответствует ожидаемому");
    }

    @Test
    @Order(19)
    void checkRemoveAllSubtasks() {
        taskManager.removeAllSubtasks();
        ArrayList<String> subtasks = taskManager.getSubtasksList();
        int amountOfSubtasks = subtasks.size();
        assertEquals(0, amountOfSubtasks,
                "Фактическое кол-во подзадач не соответствует ожидаемому");
    }

}