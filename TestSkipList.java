import java.util.concurrent.*;

public final class TestSkipList {

  public enum Setting {
    OPERATIONS, THREADS, ADDS, REMOVES, CONTAINS;
  }

  private int operations, threads;
  private double adds, removes, contains;
  private SkipList sl;
  private ExecutorService executorService;

  public TestSkipList(SkipList sl) {
    this.sl = sl;
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
    executorService.shutdown();
  }

  public void start() {
    executorService = Executors.newFixedThreadPool(threads);
    int opPerThread = operations / threads; // approximation +/- 1 can be ignored here
    for (int i = 0; i < threads; i++) {
      executorService.execute(new Runnable() {
        public void run() {
          for (int a = 0; a < opPerThread * adds; a++) {
            sl.add((int) (Math.random() * adds));
          }

          for (int r = 0; r < opPerThread * removes; r++) {
            sl.remove((int) (Math.random() * removes));
          }

          for (int c = 0; c < opPerThread * contains; c++) {
            sl.contains((int) (Math.random() * contains));
          }
        }
      });
    }
  }
}
