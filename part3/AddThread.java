import java.util.*;

public final class AddThread implements Runnable {

  private int size;
  private TestSkipList.Mode mode;
  private SkipList list;
  private History logs;

   public AddThread(History l, int size, TestSkipList.Mode mode, SkipList list) {
    this.size = size;
    this.mode = mode;
    this.list = list;
    logs = l;
  }
  
  public void run() {
    Random rand = new Random();
    switch(mode) {
      case RANDOM:
        for(int i = 0; i < 1000000; i ++) {
          double value = (Math.random() * size);
          list.add((int)value,logs);
        }
        break;
      case NORMAL:
        for(int i = 0; i < 1000000; i++) {
          double value = rand.nextGaussian() * (size/Math.sqrt(12.0)) + size/2.0;
          list.add((int)value, logs);
        }
        break;
      default:
        break;
    }

  }
}
