package handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import service.BaseHttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                get(httpExchange);
            case "POST":
                post(httpExchange);
            case "DELETE":
                delete(httpExchange);
            default:
                try (OutputStream os = httpExchange.getResponseBody()) {
                    httpExchange.sendResponseHeaders(405, 0);
                    os.write("Метод не найден".getBytes(StandardCharsets.UTF_8));
                }
        }
        httpExchange.close();
    }

    private void get(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2) {
            sendText(httpExchange, gson.toJson(taskManager.getEpics()));
        }
        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                Epic epic = taskManager.getEpicById(id.get());
                if (epic != null) {
                    sendText(httpExchange, gson.toJson(epic));
                } else {
                    sendNotFound(httpExchange, String.format("Эпик с id %d не найден", id.get()));
                }
            }
        }

        if (pathParts.length == 4) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                Epic epic = taskManager.getEpicById(id.get());
                if (epic != null) {
                    sendText(httpExchange, gson.toJson(taskManager.getAllSubtasksByEpicId(epic.getId())));
                } else {
                    sendNotFound(httpExchange, String.format("Эпик с id %d не найден", id.get()));
                }
            }
        }
    }

    private void post(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);

        if (!jsonElement.isJsonObject()) {
            sendHasInteractions(httpExchange, 406, "Не поддерживаемый формат переданного объекта");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        if (pathParts.length == 2) {
            taskManager.addNewEpic(epic);
            if (taskManager.getEpicById(epic.getId()).equals(epic)) {
                sendHasInteractions(httpExchange, 201, "Добавлен новый эпик");
            }
        }

        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.updateEpic(id.get(), epic);
                if (taskManager.getEpicById(epic.getId()).equals(epic)) {
                    sendHasInteractions(httpExchange, 201, "Эпик изменен");
                }
            }
        }
    }

    private void delete(HttpExchange httpExchange) throws IOException {
        String requestPath = httpExchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 3) {
            Optional<Integer> id = getEpicId(pathParts[2]);
            if (id.isPresent()) {
                taskManager.removeEpicById(id.get());
                if (taskManager.getEpicById(id.get()) == null) {
                    sendHasInteractions(httpExchange, 201, "Эпик удален");
                }
            }
        }
    }

    protected static Optional<Integer> getEpicId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}