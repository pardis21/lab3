import java.util.*;

public class LogThread implements Runnable {

  private History logs;
  private SkipList list;
  private int operations;
  private double adds, removes, contains;

  public LogThread(History l, SkipList sl, int op, double a, double r, double c) {
    logs = l;
    operations = op;
    list = sl;
    adds = a;
    removes = r;
    contains = c;
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
  }
  
}
