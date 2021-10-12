import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.*;

public class MPSC {
    AtomicReference<Node> head, tail;

    public MPSC() {
        Node node = new Node(null);
        head = new AtomicReference<Node>(node);
        tail = new AtomicReference<Node>(node);
    }

    public void enq(Log value) {
        Node node = new Node(value);
        while (true) {
            Node last = tail.get();
            Node next = last.next.get();
            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(next, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    public Log deq() throws EmptyStackException {
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        throw new EmptyStackException();
                    }
                    tail.compareAndSet(last, next);
                } else {
                    Log value = next.value;
                    if (head.compareAndSet(first, next))
                        return value;
                }
            }
        }
    }

    public boolean isEmpty() {
        Node first = head.get();
        Node last = tail.get();
        Node next = first.next.get();

        return (first == head.get() && first == last && next == null);
    }
}

class Node {
    public Log value;
    public AtomicReference<Node> next;

    public Node(Log value) {
        this.value = value;
        next = new AtomicReference<Node>(null);
    }
}
