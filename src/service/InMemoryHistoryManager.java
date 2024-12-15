package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final HistoryLinkedList<Task> history = new HistoryLinkedList<>();

    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        int id = task.getId();
        if (history.historyMap.containsKey(id)) {
            history.removeNode(history.historyMap.get(id));
        }
        history.linkLast(node);
        history.historyMap.put(id, history.getTail());
    }

    @Override
    public void remove(int id) {
        if (history.historyMap.get(id) != null) {
            history.removeNode(history.historyMap.get(id));
            history.historyMap.remove(id);
        } else {
            System.out.println("Задача отсутствует в списке");
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    public static class HistoryLinkedList<T> {
        private static Map<Integer, Node> historyMap = new HashMap<>();
        private Node<T> head;
        private Node<T> tail;

        public Node<T> getTail() {
            return tail;
        }

        public void linkLast(Node<T> node) {
            final Node<T> oldTail = tail;
            node.prev = oldTail;
            tail = node;
            if (oldTail == null) {
                head = node;
            } else {
                oldTail.next = node;
            }
        }

        public List<T> getTasks() {
            List<T> tmpTasks = new ArrayList<>();
            Node<T> curHead = head;
            while (curHead != null) {
                tmpTasks.add(curHead.data);
                curHead = curHead.next;
            }
            return tmpTasks;
        }

        void removeNode(Node<T> node) {
            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;
            if (prevNode == null && nextNode == null) {
                head = null;
                tail = null;
            } else if (prevNode == null && nextNode != null) {
                head = nextNode;
                nextNode.prev = null;
            } else if (prevNode != null && nextNode == null) {
                tail = prevNode;
                prevNode.next = null;
            } else {
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            }
        }
    }
}