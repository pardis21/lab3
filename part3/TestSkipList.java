import java.util.concurrent.*;
import java.util.*;
import java.util.stream.Collectors;


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
  private History history;

  public TestSkipList(SkipList sl, Mode mode) {
    this.sl = sl;
    this.mode = mode;
    history = new History();
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
  
  public void populate(int size) {
    
    List<List<Log>> histories = new LinkedList<List<Log>>();

    if(size > 1000000) {
      int numberThreads = size/1000000;
      es = Executors.newFixedThreadPool(numberThreads);
      for(int i = 0; i < numberThreads; i++) {
        History h = new History();
        es.execute(new AddThread(h, size, mode, sl));
        histories.add(h.list());
      }
      this.stop();
      histories.add(history.list());
      history = new History(History.merge(histories));
    } else {
      double value;
      History h = new History();
      switch(mode) {
        case RANDOM:
          for(int i = 0; i < size; i++) {
            value = Math.random() * size;
            sl.add((int)value, h);
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
            sl.add((int)value, h);
          }
          break;
      }
      histories.add(h.list()); histories.add(history.list());
      
      history = new History(History.merge(histories));
    }
  }

  public void stop() {
    es.shutdown();
    try {
      if (!es.awaitTermination(3600, TimeUnit.SECONDS)) {
        es.shutdownNow();
      }
    } catch (InterruptedException ex) {
      es.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public void start() {
    es = Executors.newFixedThreadPool(threads);
    int opPerThread = operations / threads; // approximation +/- 1 can be ignored here
    List<List<Log>> histories = new LinkedList<List<Log>>();
    for(int i = 0; i < threads; i++) {
      History tmp = new History();
      es.execute(new LogThread(tmp, sl, opPerThread, adds, removes, contains));
      histories.add(tmp.list());
    }
    this.stop();
    histories.add(history.list());
    history = new History(History.merge(histories));
  }
  
  public History getHistory() { return history; }

}
