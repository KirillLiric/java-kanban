import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Уборка", "Помыть полы");
        taskManager.addTask(task1);
        Task task2 = new Task("Поливка", "Полить цветы");
        taskManager.addTask(task2);

        Epic epic1 = new Epic("Праздник", "Подготовка к празднику");
        taskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask("Праздник", "Испечь торт", epic1.getId());
        taskManager.addSubtask(subtask11);

        Epic epic2 = new Epic("Переезд", "Подготовка к переезду");
        taskManager.addEpic(epic2);
        Subtask subtask21 = new Subtask("Переезд", "Собрать вещи", epic2.getId());
        taskManager.addSubtask(subtask21);
        Subtask subtask22 = new Subtask("Переезд", "Заказать транспорт", epic2.getId());
        taskManager.addSubtask(subtask22);

        System.out.println("\nСписки задач");

        System.out.println(taskManager.getTaskMap());
        System.out.println(taskManager.getEpicMap());
        System.out.println(taskManager.getSubtaskMap());

        System.out.println("\nСтатусы задач");
        for (Integer i : taskManager.getTaskMap().keySet()) {
            System.out.println(taskManager.getTaskMap().get(i).getStatus());
        }
        System.out.println("Статусы эпиков");
        for (Integer i : taskManager.getEpicMap().keySet()) {
            System.out.println(taskManager.getEpicMap().get(i).getStatus());
        }
        System.out.println("Статусы подзадач");
        for (Integer i : taskManager.getSubtaskMap().keySet()) {
            System.out.println(taskManager.getSubtaskMap().get(i).getStatus());
        }

        System.out.println("\nИзменение статуса задач");
        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        System.out.println("Статусы задач");
        for (Integer i : taskManager.getTaskMap().keySet()) {
            System.out.println(taskManager.getTaskMap().get(i).getStatus());
        }

        System.out.println("\nИзменение статуса подзадачи");
        subtask11.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask11);
        System.out.println("Статус подзадачи " + subtask11.getStatus());
        System.out.println("Статус эпика " + epic1.getStatus());

        System.out.println("\nИзменение статуса подзадачи второго эпика");
        subtask21.setStatus(Status.IN_PROGRESS);
        subtask22.setStatus(Status.NEW);
        epic2.setStatus(Status.NEW); //Проверка на ручное изменение статуса
        taskManager.updateSubtask(subtask21);
        taskManager.updateSubtask(subtask22);
        taskManager.updateEpic(epic2);
        System.out.println("Статус первой подзадачи " + subtask21.getStatus());
        System.out.println("Статус второй подзадачи " + subtask22.getStatus());
        System.out.println("Статус эпика " + epic2.getStatus());

        System.out.println("\nУдаление одной задачи");
        System.out.println("До " + taskManager.getTaskMap());
        taskManager.deleteTask(task1.getId());
        System.out.println("После " + taskManager.getTaskMap());

        System.out.println("\nУдаление эпика");
        System.out.println("Эпики до " + taskManager.getEpicMap());
        System.out.println("Подзадачи до " + taskManager.getSubtaskMap());
        taskManager.deleteEpic(epic1.getId());
        System.out.println("Эпики после " + taskManager.getEpicMap());
        System.out.println("Подзадачи после " + taskManager.getSubtaskMap());
    }
}

