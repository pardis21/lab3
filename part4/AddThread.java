import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class AddThread implements Runnable {

  private int size;
  private TestSkipList.Mode mode;
  private SkipList list;
  private MPSC buffer;
  private AtomicInteger finished;

   public AddThread(MPSC buffer, int size, TestSkipList.Mode mode, SkipList list, AtomicInteger finished) {
    this.size = size;
    this.mode = mode;
    this.list = list;
    this.buffer = buffer;
    this.finished = finished;
  }
  
  public void run() {
    Random rand = new Random();
    switch(mode) {
      case RANDOM:
        for(int i = 0; i < 1000000; i ++) {
          double value = (Math.random() * size);
          list.add((int)value,buffer);
        }
        break;
      case NORMAL:
        for(int i = 0; i < 1000000; i++) {
          double value = rand.nextGaussian() * (size/Math.sqrt(12.0)) + size/2.0;
          list.add((int)value,buffer);
        }
        break;
      default:
        break;
    }

    finished.decrementAndGet();
  }
}
