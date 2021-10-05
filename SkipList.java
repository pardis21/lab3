import java.util.LinkedList;
import java.util.List;
import java.util.stream.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.*;

public final class SkipList<T> {
    static final int MAX_LEVEL = 10;
    final Node<T> head = new Node<T>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<T>(Integer.MAX_VALUE);

    public SkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<SkipList.Node<T>>(tail, false);
        }
    }

    public static final class Node<T> {
        final T value;
        final int key;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;

        // constructor for sentinel nodes
        public Node(int tmp) {
            value = null;
            key = tmp;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        // constructor for ordinary nodes
        public Node(T x, int height) {
            value = x;
            key = x.hashCode();
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = height;
        }

        // for debugging
        public void printVal() {
            System.out.println("Value: " + value + ", Level: " + topLevel + ", Key: " + key);
        }
    }

    boolean add(T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, preds, succs);
            if (found) {
                return false;
            } else {
                Node<T> newNode = new Node(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        find(x, preds, succs);
                    }
                }
                return true;
            }
        }
    }

    boolean remove(T x) {
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, preds, succs);
            if (!found) {
                return false;
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = { false };
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = { false };
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, preds, succs);
                        return true;
                    } else if (marked[0])
                        return false;
                }
            }
        }
    }

    boolean contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = { false };
        Node<T> pred = head, curr = null, succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = curr.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = pred.next[level].getReference();
                    succ = curr.next[level].get(marked);
                }
                if (curr.key < v) {
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        return (curr.key == v);
    }

    boolean find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = { false };
        boolean snip;
        Node<T> pred = null, curr = null, succ = null;
        retry: while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip)
                            continue retry;
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key) {
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            }
            return (curr.key == key);
        }
    }

    private int randomLevel() {
        return (int) (Math.random() * MAX_LEVEL);
    }

    public void printList() {
        for (int level = 0; level <= MAX_LEVEL; level++) {
            System.out.println("Printing Level " + level);
            Node<T> cur = head.next[level].getReference();
            while (cur.key < tail.key) {
                cur.printVal();
                cur = cur.next[level].getReference();
            }
            System.out.println("--------------------------------------------------");
        }
    }
}