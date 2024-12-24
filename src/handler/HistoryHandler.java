package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.BaseHttpHandler;
import service.HistoryManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    HistoryManager historyTaskManager;
    private final Gson gson;

    public HistoryHandler(HistoryManager historyTaskManager, Gson gson) {
        this.historyTaskManager = historyTaskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("GET")) {
            get(httpExchange);
        } else {
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
            sendText(httpExchange, gson.toJson(historyTaskManager.getHistory()));
        }
    }
}
