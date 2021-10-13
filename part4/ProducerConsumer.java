import java.util.Queue;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;


public class ProducerConsumer {
  private MPSC buffer = new MPSC();
  Comparator<Log> timeComparator = Comparator.comparing(Log::getTime);
  SortedSet<Log> sortedSamples = new TreeSet<Log>(timeComparator);

  public class Producer implements Runnable {

    Log sample;

    public void setLog(Log log) {
      sample = log;
    }

    public void run() {
      try {
        buffer.enq(sample);
        buffer.notify();
      } catch (Exception e) {
        System.err.println(e);
      }
    }
  }

  public class Consumer implements Runnable {
    public void run() {
      if (buffer.isEmpty()) {
        try {
          buffer.wait();
        } catch (Exception e) {
          System.err.println(e);
        }
      }
      Log sample = buffer.deq();
      sortedSamples.add(sample);
    }
  } 

  public String getSortedSamples() {
    return sortedSamples.toString();
  }
}
