import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {

    private MPSC logs;
    private SortedSet<Log> sortedSamples;
    private AtomicInteger finished;

    public Consumer(MPSC logs, SortedSet<Log> ss, AtomicInteger finished) {
        this.logs = logs;
        sortedSamples = ss;
        this.finished = finished;
    }

    public void run() {
      while(finished.getPlain() != 0 || !logs.isEmpty()) {
        if (!logs.isEmpty()) {
            Log sample = logs.deq();
            sortedSamples.add(sample);
        }
      }
    }
  } 