package managers;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList customList = new CustomLinkedList();
    private final HashMap<Integer, Node> historyMap = new HashMap<>();

    //Добавление задачи в историю

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node existingNode = historyMap.get(task.getId());
            customList.remove(existingNode);
        }
        Node newNode = customList.linkLast(task);
        historyMap.put(task.getId(), newNode);
    }

    //Получение истории просмотров

    @Override
    public ArrayList<Task> getHistory() {
        return customList.getTasks();
    }

    //Удаление просмотра
    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
        }
    }

    private void removeNode(Node node) {
        if (historyMap.containsKey(node.task.getId())) {
            Node nodeToRemove = historyMap.get(node.task.getId());
            customList.remove(nodeToRemove);
            historyMap.remove(node.task.getId());
        }
    }

    //Своя структура
    class CustomLinkedList {
        private Node head;
        private Node tail;

        public Node linkLast(Task task) {
            Node newNode = new Node(task);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            return newNode;
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node current = head;
            while (current != null) {
                tasks.add(current.task);
                current = current.next;
            }
            return tasks;
        }

        public void remove(Node node) {
            if (node == null) return;

            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
        }

    }
}

//Узел
class Node {
    Task task;
    Node prev;
    Node next;

    Node(Task task) {
        this.task = task;
    }
}




