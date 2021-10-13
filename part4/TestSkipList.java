import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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
  private MPSC buffer;
  SortedSet<Log> sortedSamples;

  public TestSkipList(SkipList sl, Mode mode) {
    this.sl = sl;
    this.mode = mode;
    buffer = new MPSC();
    Comparator<Log> timeComparator = Comparator.comparing(Log::getTime);
    sortedSamples = new TreeSet<Log>(timeComparator);
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

    if (size > 100000) {
      int numberThreads = size / 100000;

      if (numberThreads == 1)
        numberThreads = 2;

      AtomicInteger finished = new AtomicInteger(numberThreads - 1);

      es = Executors.newFixedThreadPool(numberThreads);
      for (int i = 0; i < numberThreads - 1; i++) {
        es.execute(new AddThread(buffer, size, mode, sl, finished));
      }
      es.execute(new Consumer(buffer, sortedSamples, finished));
      this.stop();
    } else {
      double value;
      switch (mode) {
        case RANDOM:
          for (int i = 0; i < size; i++) {
            value = Math.random() * size;
            sl.add((int) value, buffer);
          }
          break;
        case NORMAL:
          Random rand = new Random();
          for (int i = 0; i < size; i++) {
            value = (rand.nextGaussian() * (size / Math.sqrt(12.0)) + size / 2.0);
            if (value > size)
              value = size;
            else if (value < 0)
              value = 0;
            sl.add((int) value, buffer);
          }
          break;
      }
      // es = Executors.newFixedThreadPool(1);
      // es.execute(new Consumer(buffer, sortedSamples));
      this.stop();
    }
  }

  public void stop() {
    es.shutdown();
    try {
      if (!es.awaitTermination(20, TimeUnit.SECONDS)) {
        es.shutdownNow();
      }
    } catch (InterruptedException ex) {
      es.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public void start() {
    AtomicInteger finished = new AtomicInteger(threads - 1);

    es = Executors.newFixedThreadPool(threads);
    int opPerThread = operations / threads; // approximation +/- 1 can be ignored here
    for (int i = 0; i < threads - 1; i++) {
      es.execute(new LogThread(buffer, sl, opPerThread, adds, removes, contains, finished));
    }
    es.execute(new Consumer(buffer, sortedSamples, finished));
    this.stop();
  }

  public boolean getHistory() {
    LinkedList<Log> tmp = new LinkedList<Log>();
    for (Log sample : sortedSamples) {
      tmp.add(sample);
    }
    History h = new History(tmp);
    return h.check();
  }

}
