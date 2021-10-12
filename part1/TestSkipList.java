import java.util.concurrent.*;
import java.util.*;


public final class TestSkipList {

  public enum Setting {
    OPERATIONS, THREADS, ADDS, REMOVES, CONTAINS;
  }

  public enum Mode {
    RANDOM, NORMAL;
  }

  private int operations, threads;
  private Mode mode;
  private double adds, removes, contains;
  private SkipList sl;
  private ExecutorService es;

  public TestSkipList(SkipList sl, Mode mode) {
    this.sl = sl;
    this.mode = mode;
  }

  public void set(int op, int t, double a, double r, double c) {
    operations = op;
    threads = t;
    adds = a;
    removes = r;
    contains = c;
  }

  public void set(Setting s, double value) {
    switch (s) {
      case OPERATIONS:
        operations = (int) value;
        break;
      case THREADS:
        threads = (int) value;
        break;
      case ADDS:
        adds = value;
        break;
      case REMOVES:
        removes = value;
        break;
      case CONTAINS:
        contains = value;
        break;
      default:
    }
  }

  public void stop() {
    es.shutdown();
  }

  public void populate(int size) {
    if(size > 1000000) {
      int numberThreads = size/1000000;
      es = Executors.newFixedThreadPool(numberThreads);
      for(int i = 0; i < numberThreads; i++) {
        es.execute(new AddThread(size, mode, sl));
      }
      es.shutdown();
    } else {
      double value;
      switch(mode) {
        case RANDOM:
          for(int i = 0; i < size; i++) {
            value = Math.random() * size;
            sl.add((int)value);
          }
          break;
        case NORMAL:
          Random rand = new Random();
          for(int i = 0; i < size; i++) {
            value = (rand.nextGaussian() *(size / Math.sqrt(12.0)) + size/2.0);
            if (value > size)
              value = size;
            else if (value < 0)
              value = 0;
            sl.add((int)value);
          }
          break;
      }
    }
  }

  public void start() {
    es = Executors.newFixedThreadPool(threads);
    int opPerThread = operations / threads; // approximation +/- 1 can be ignored here

    for (int i = 0; i < threads; i++) {
      es.execute(new Runnable() {
          public void run() {
          for (int a = 0; a < opPerThread * adds; a++) {
          sl.add((int) (Math.random() * operations));
          }

          for (int r = 0; r < opPerThread * removes; r++) {
          sl.remove((int) (Math.random() * operations));
          }

          for (int c = 0; c < opPerThread * contains; c++) {
          sl.contains((int) (Math.random() * operations));
          }
          }
          });
    }
  }
}
