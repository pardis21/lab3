import java.util.*;

class Main {

  public static void main(String[] args) {
    // random distribution
    SkipList slRand = new SkipList();

    for (int i = 0; i < 100000; i++) {
      slRand.add((int) (Math.random() * 10000000));
    }

    slRand.printStats();

    // normal distribution
    SkipList slNorm = new SkipList();
    Random rand = new Random();
    for (int i = 0; i < 100000; i++) {
      slNorm.add((int)(rand.nextGaussian()*2886751+5000000));
    }

    slNorm.printStats();
  }
}