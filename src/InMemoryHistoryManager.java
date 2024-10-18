import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager{

    private static LinkedList<Task> historyList = new LinkedList<>();

    //Добавление задачи в историю

    @Override
    public void add(Task task) {
        if(historyList.size() == 10) {
            historyList.removeFirst();
            historyList.add(task);
        } else {
            historyList.add(task);
        }
    }

    //Получение истории просмотров

    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }
}
