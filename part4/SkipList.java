import java.math.BigDecimal;
import java.util.concurrent.atomic.*;
import java.util.*;
import java.math.MathContext; 
import java.io.*;


public final class SkipList {
    static final int MAX_LEVEL = 10;
    final Node head = new Node(Integer.MIN_VALUE);
    final Node tail = new Node(Integer.MAX_VALUE);

    public SkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<SkipList.Node>(tail, false);
        }
    }

    public static final class Node {
        final int value;
        final int key;
        final AtomicMarkableReference<Node>[] next;
        private int topLevel;

        // constructor for sentinel nodes
        public Node(int tmp) {
            value = -1;
            key = tmp;
            next = (AtomicMarkableReference<Node>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        // constructor for ordinary nodes
        public Node(int x, int height) {
            value = x;
            key = x;
            next = (AtomicMarkableReference<Node>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node>(null, false);
            }
            topLevel = height;
        }

        // for debugging
        public void printVal() {
            System.out.println("Value: " + value + ", Level: " + topLevel + ", Key: " + key);
        }
    }

    boolean add(int x, MPSC logs) {
      int topLevel = randomLevel();
      int bottomLevel = 0;
      Node[] preds = (Node[]) new Node[MAX_LEVEL + 1];
      Node[] succs = (Node[]) new Node[MAX_LEVEL + 1];
      while (true) {
        boolean found = find(x, preds, succs);
          if (found) {
            logs.enq(new Log(Log.Method.ADD,false,x,System.nanoTime()));
            
            return false;
          }
        Node newNode = new Node(x, topLevel);
        for (int level = bottomLevel; level <= topLevel; level++) {
          Node succ = succs[level];
          newNode.next[level].set(succ, false);
        }
        Node pred = preds[bottomLevel];
        Node succ = succs[bottomLevel];
          if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
            continue;
          }
          logs.enq(new Log(Log.Method.ADD,true,x,System.nanoTime()));
          
        for (int level = bottomLevel + 1; level <= topLevel; level++) {
          while (true) {
            pred = preds[level];
            succ = succs[level];
            if (pred.next[level].compareAndSet(succ, newNode, false, false)){
              break;
            }
            find(x, preds, succs);
          }
        }
        return true;
      }
    }

    boolean remove(int x, MPSC logs) {
      int bottomLevel = 0;
      Node[] preds = (Node[]) new Node[MAX_LEVEL + 1];
      Node[] succs = (Node[]) new Node[MAX_LEVEL + 1];
      Node succ;
      while (true) {
        boolean found = find(x, preds, succs);
          if (!found) {
            logs.enq(new Log(Log.Method.REMOVE,false,x,System.nanoTime()));
            
            return false;
          } 
        Node nodeIntegeroRemove = succs[bottomLevel];
        for (int level = nodeIntegeroRemove.topLevel; level >= bottomLevel + 1; level--) {
          boolean[] marked = { false };
          succ = nodeIntegeroRemove.next[level].get(marked);
          while (!marked[0]) {
            nodeIntegeroRemove.next[level].compareAndSet(succ, succ, false, true);
            succ = nodeIntegeroRemove.next[level].get(marked);
          }
        }
        boolean[] marked = { false };
        succ = nodeIntegeroRemove.next[bottomLevel].get(marked);
        int i = 0;
        while (true) {
          boolean iMarkedIt; 
            iMarkedIt = nodeIntegeroRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
            if(iMarkedIt)
              logs.enq(new Log(Log.Method.REMOVE,true,x,System.nanoTime()));
              
          succ = succs[bottomLevel].next[bottomLevel].get(marked);
          if (iMarkedIt) {
            find(x, preds, succs);
            return true;
          } else if (marked[0])
            return false;
        }
      }
    
    }

    boolean contains(int x, MPSC logs) {
      int bottomLevel = 0;
      int v = x;
      boolean[] marked = { false };
      Node pred = head, curr = null, succ = null;
      for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
          curr = pred.next[level].getReference();
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
        logs.enq(new Log(Log.Method.CONTAINS,curr.key==v,x,System.nanoTime()));
        
        return (curr.key == v);
    }

    boolean find(int x, Node[] preds, Node[] succs) {
        int bottomLevel = 0;
        int key = x;
        boolean[] marked = { false };
        boolean snip;
        Node pred = null, curr = null, succ = null;
        retry: while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) { //succ is a logically removed node
                        snip = pred.next[level].compareAndSet(curr, succ, false, false); //physically removing node
                        if (!snip) //try again if collision with an other thread
                            continue retry;
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key) { //keep going while key is less than what we are looking for
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr; //
            }
            return (curr.key == key);
        }
    }

    private int randomLevel() {
        int level = 0;
        boolean next = true;
        while(level < MAX_LEVEL && next) {
          if(Math.random() < 0.5) {
            level ++;
          } else {
            next = false;
          }
        }
        return level;
        //return (int) (Math.random() * MAX_LEVEL);
    }

    public void printList() {
        for (int level = 0; level <= MAX_LEVEL; level++) {
            System.out.println("Printing Level " + level);
            Node cur = head.next[level].getReference();
            while (cur.key < tail.key) {
                cur.printVal();
                cur = cur.next[level].getReference();
            }
            System.out.println("--------------------------------------------------");
        }
    }

    public void plotValues(String f) {
      try {
        Node cur = head.next[0].getReference();
        BufferedWriter writer = new BufferedWriter(new FileWriter(f+".csv"));

        while(cur.key < tail.key){
          //add to output file
          writer.write(cur.value+"\n");
          cur = cur.next[0].getReference();
        }
        writer.close();
      } catch(Exception e) {
        System.err.println(e);
      }
    }
    
    public void printStats() {
        System.out.println("Printing Stats for Skip List");
        BigDecimal mean = BigDecimal.valueOf(0);
        int length = 0;
        Node cur = head.next[0].getReference();
        while (cur.key < tail.key) {
            mean = mean.add(BigDecimal.valueOf(cur.value));
            length++;
            cur = cur.next[0].getReference();
        }
        mean = mean.divide(BigDecimal.valueOf(length),  MathContext.DECIMAL128);
        
        BigDecimal varSum =  BigDecimal.valueOf(0);;
        cur = head.next[0].getReference();
        while (cur.key < tail.key) {
            BigDecimal curVal = BigDecimal.valueOf(cur.value);
            curVal = curVal.subtract(mean);
            curVal = curVal.pow(2);
            varSum = varSum.add(curVal);
            cur = cur.next[0].getReference();
        }

        int tmp = length - 1;
        BigDecimal variance = varSum.divide(BigDecimal.valueOf(tmp), MathContext.DECIMAL32);

        System.out.println("Mean: " + mean.toString());
        System.out.println("Variance: " + variance.toString());
        System.out.println("--------------------------------------------------");
    }
}
