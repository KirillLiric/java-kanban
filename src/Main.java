import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Managers.getDefault();
/*
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();



        Task task1 = new Task("Уборка", "Помыть полы");
        inMemoryTaskManager.addTask(task1);
        Task task2 = new Task("Поливка", "Полить цветы");
        inMemoryTaskManager.addTask(task2);

        Epic epic1 = new Epic("Праздник", "Подготовка к празднику");
        inMemoryTaskManager.addEpic(epic1);
        Subtask subtask11 = new Subtask("Праздник", "Испечь торт", epic1.getId());
        inMemoryTaskManager.addSubtask(subtask11);

        Epic epic2 = new Epic("Переезд", "Подготовка к переезду");
        inMemoryTaskManager.addEpic(epic2);
        Subtask subtask21 = new Subtask("Переезд", "Собрать вещи", epic2.getId());
        inMemoryTaskManager.addSubtask(subtask21);
        Subtask subtask22 = new Subtask("Переезд", "Заказать транспорт", epic2.getId());
        inMemoryTaskManager.addSubtask(subtask22);


        System.out.println("\nСписки задач");

        System.out.println(inMemoryTaskManager.getTaskMap());
        System.out.println(inMemoryTaskManager.getEpicMap());
        System.out.println(inMemoryTaskManager.getSubtaskMap());

        System.out.println("\nСтатусы задач");
        for (Integer i : inMemoryTaskManager.getTaskMap().keySet()) {
            System.out.println(inMemoryTaskManager.getTaskMap().get(i).getStatus());
        }
        System.out.println("Статусы эпиков");
        for (Integer i : inMemoryTaskManager.getEpicMap().keySet()) {
            System.out.println(inMemoryTaskManager.getEpicMap().get(i).getStatus());
        }
        System.out.println("Статусы подзадач");
        for (Integer i : inMemoryTaskManager.getSubtaskMap().keySet()) {
            System.out.println(inMemoryTaskManager.getSubtaskMap().get(i).getStatus());
        }

        System.out.println("\nИзменение статуса задач");
        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        inMemoryTaskManager.updateTask(task2);
        System.out.println("Статусы задач");
        for (Integer i : inMemoryTaskManager.getTaskMap().keySet()) {
            System.out.println(inMemoryTaskManager.getTaskMap().get(i).getStatus());
        }

        System.out.println("\nИзменение статуса подзадачи");
        subtask11.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask11);
        System.out.println("Статус подзадачи " + subtask11.getStatus());
        System.out.println("Статус эпика " + epic1.getStatus());

        System.out.println("\nИзменение статуса подзадачи второго эпика");
        subtask21.setStatus(Status.NEW);
        subtask22.setStatus(Status.DONE);
        epic2.setStatus(Status.NEW); //Проверка на ручное изменение статуса
        inMemoryTaskManager.updateSubtask(subtask21);
        inMemoryTaskManager.updateSubtask(subtask22);
        inMemoryTaskManager.updateEpic(epic2);
        System.out.println("Статус первой подзадачи " + subtask21.getStatus());
        System.out.println("Статус второй подзадачи " + subtask22.getStatus());
        System.out.println("Статус эпика " + epic2.getStatus());

        System.out.println("\nУдаление одной задачи");
        System.out.println("До " + inMemoryTaskManager.getTaskMap());
        inMemoryTaskManager.deleteTask(task1.getId());
        System.out.println("После " + inMemoryTaskManager.getTaskMap());

        System.out.println("\nУдаление эпика");
        System.out.println("Эпики до " + inMemoryTaskManager.getEpicMap());
        System.out.println("Подзадачи до " + inMemoryTaskManager.getSubtaskMap());
        inMemoryTaskManager.deleteEpic(epic1.getId());
        System.out.println("Эпики после " + inMemoryTaskManager.getEpicMap());
        System.out.println("Подзадачи после " + inMemoryTaskManager.getSubtaskMap());*/
    }
}

