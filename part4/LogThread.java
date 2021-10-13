import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LogThread implements Runnable {

  private MPSC logs;
  private SkipList list;
  private int operations;
  private double adds, removes, contains;
  private AtomicInteger finished;

  public LogThread(MPSC l, SkipList sl, int op, double a, double r, double c, AtomicInteger finished) {
    logs = l;
    operations = op;
    list = sl;
    adds = a;
    removes = r;
    contains = c;
    this.finished = finished;
  }

  public void run() {
    for (int a = 0; a < operations * adds; a++) {
      list.add((int) (Math.random() * operations), logs);
    }

    for (int r = 0; r < operations * removes; r++) {
      list.remove((int) (Math.random() * operations), logs);
    }

    for (int c = 0; c < operations * contains; c++) {
      list.contains((int) (Math.random() * operations), logs);
    }

    finished.decrementAndGet();
  }
  
}
