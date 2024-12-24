package service;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    String BASE_URL = "http://localhost:8081";
    HistoryManager historyTaskManager = Managers.getDefaultHistory();;
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();
    HttpClient client;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
        InMemoryTaskManager.id = 1;
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        List<Task> tasks = historyTaskManager.getHistory();
        for (Task task : tasks) {
            historyTaskManager.remove(task.getId());
        }
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(55), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertNotNull(manager.getTasks()),
                () -> assertEquals(1, manager.getTasks().size()),
                () -> assertEquals("Переезд", manager.getTasks().get(0).getName())
        );
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(55), LocalDateTime.now());
        Task task2 = new Task("Покупки", "Хлеб Молоко Корм для щенка", Duration.ofMinutes(15), LocalDateTime.now().plusHours(1));
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertAll(
                () -> assertEquals(200, response.statusCode()),
                () -> assertEquals(2, tasks.length)
        );
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(55), LocalDateTime.now());
        manager.addNewTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertTrue(manager.getTasksList().isEmpty())
        );
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Переезд", "Собрать коробки Упаковать цветы Передать ключи", Duration.ofMinutes(55), LocalDateTime.now());
        manager.addNewTask(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertAll(
                () -> assertEquals(200, response.statusCode()),
                () -> assertEquals(task.getId(), returnedTask.getId()),
                () -> assertEquals("Переезд", returnedTask.getName())
        );
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        manager.addNewEpic(epic);
        manager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(90), epic.getId()));
        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<String> epics = manager.getEpicsList();
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals(2, epics.size()),
                () -> assertEquals(epic.toString(), epics.get(0))
        );
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        Epic epic2 = new Epic("Путешествие", "План отдыха");
        manager.addNewEpic(epic1);
        manager.addNewEpic(epic2);
        manager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now(), epic1.getId()));
        manager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу, Пляжный отдых, Подняться на гору", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(80), epic2.getId()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertAll(
                () -> assertEquals(200, response.statusCode()),
                () -> assertEquals(2, epics.length)
        );
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        manager.addNewEpic(epic);
        Subtask subtask = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = manager.getSubtasks();
        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals(1, subtasks.size()),
                () -> assertEquals("Дизайн квартиры", subtasks.get(0).getName())
        );
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры");
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now(), epic.getId());
        Subtask subtask2 = new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(180), epic.getId());
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertAll(
                () -> assertEquals(200, response.statusCode()),
                () -> assertEquals(2, subtasks.length)
        );
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        Task task1 = new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи", 1, Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("Покупки", "Хлеб, Молоко, Корм для щенка", 2, Status.DONE, Duration.ofMinutes(20), LocalDateTime.now().plusHours(1));
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertAll(
                () -> assertEquals(2, tasks.length),
                () -> assertEquals(tasks[0], task1),
                () -> assertEquals(tasks[1], task2)
        );
    }
}
