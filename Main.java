import java.util.*;

class Main {

  public static void main(String[] args) {

    final int LAB_VALUE = 10000000;
    final int SIZE = (args.length > 0)?Integer.parseInt(args[0]):LAB_VALUE;
    // random distribution
    SkipList slRand = new SkipList();
    final double MEAN = SIZE/2.0;
    final double STD_DEV = SIZE/Math.sqrt(12.0);
    
    System.out.println("Estimated mean: "+MEAN);
    System.out.println("Estimated std deviation: "+STD_DEV);
    System.out.println("============================================");   
    long begin = System.nanoTime();
    for (int i = 0; i < SIZE; i++) {
      double value = (Math.random() * SIZE);
      slRand.add((int)value);
    }
    System.out.println("duration: "+ (System.nanoTime() - begin));
    slRand.plotValues("rand");
    //slRand.printList();
    slRand.printStats();

    /*// normal distribution
    SkipList slNorm = new SkipList();
    Random rand = new Random();
    begin = System.nanoTime();
    for (int i = 0; i < SIZE; i++) {
      double value = (rand.nextGaussian()*STD_DEV+MEAN);
      if(value > SIZE)
        value = SIZE;
      else if (value < 0)
        value = 0;
      slNorm.add((int)value);
    }
    System.out.println("duration: "+ (System.nanoTime() - begin));
    slNorm.plotValues("norm");

    //slNorm.printList();
    slNorm.printStats();*/

    TestSkipList test = new TestSkipList(slRand);
    test.set(100, 3, 0.1, 0.3, 0.6);
    test.start();
    test.stop();
    slRand.printStats();

  }
}
