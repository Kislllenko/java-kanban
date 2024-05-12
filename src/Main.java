import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        taskManager.addNewTask(new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи"));
        taskManager.addNewTask(new Task("Покупки", "Хлеб, Молоко, Корм для щенка"));
        taskManager.addNewEpic(new Epic("Ремонт квартиры", "Ремонт, Дизайн квартиры" ));
        taskManager.addNewSubtask(new Subtask("Дизайн квартиры", "Референсы, 3D визуализация, Смета", 3));
        taskManager.addNewSubtask(new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3));
        taskManager.addNewEpic(new Epic("Путешествие", "План отдыха"));
        taskManager.addNewSubtask(new Subtask("План отдыха", "Прогулка по городу, Пляжный отдых, Подняться на гору", 6));

        taskManager.getTaskList();
        taskManager.getEpicList();
        taskManager.getSubtaskList();

        taskManager.updateTask(1, new Task("Переезд", "Собрать коробки, Упаковать цветы, Передать ключи", Status.IN_PROGRESS));
        taskManager.updateTask(2, new Task("Покупки", "Хлеб, Молоко, Корм для щенка", Status.DONE));
        taskManager.updateSubtask(4, new Subtask("Дизайн офиса", "Рефы, Визуализация, Стоимость работ", 3, Status.DONE));
        taskManager.updateSubtask(5, new Subtask("Ремонтные работы", "Покрытие полов, Покраска стен, Установка кухни", 3, Status.IN_PROGRESS));

        taskManager.getTaskList();
        taskManager.getEpicList();
        taskManager.getSubtaskList();

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);

        taskManager.getEpicList();
        taskManager.getTaskList();

    }
}
