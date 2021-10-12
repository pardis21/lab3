import java.util.*;
import java.io.*;
import java.util.concurrent.*;

class Main {

  final static int LAB_VALUE = 10000000;
  final static int NUM_TESTS = 10;
  final static int NUM_OPERATIONS = 1000000;
  public static void main(String[] args) {

    final int SIZE = (args.length > 0) ? Integer.parseInt(args[0]) : LAB_VALUE;

    SkipList slRand = new SkipList();
    SkipList slNorm = new SkipList();
    TestSkipList testRand = new TestSkipList(slRand, TestSkipList.Mode.RANDOM);
    TestSkipList testNorm = new TestSkipList(slNorm, TestSkipList.Mode.NORMAL);

    final double MEAN = SIZE / 2.0;
    final double STD_DEV = SIZE / Math.sqrt(12.0);

    System.out.println("Estimated mean: " + MEAN);
    System.out.println("Estimated variance: " + STD_DEV*STD_DEV);
    System.out.println("============================================");
    long begin = System.nanoTime();
    testRand.populate(SIZE);
    testNorm.populate(SIZE);
    System.out.println("Both list populated in: " + (System.nanoTime() - begin)/1000000000 + "s");
    slRand.plotValues("rand");
    slNorm.plotValues("norm");
    slRand.printStats();
    slNorm.printStats();

    
   try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("part3.csv"));

      System.out.println("====2 threads====");
      System.out.println("**step 1**");
      part3(testRand, testNorm, 2, 0.1, 0.1, 0.8, writer);
      System.out.println("**step 2**");
      part3(testRand, testNorm, 2, 0.5, 0.5, 0.0, writer);
      System.out.println("**step 3**");
      part3(testRand, testNorm, 2, 0.5, 0.25, 0.25, writer);
      System.out.println("**step 4**");
      part3(testRand, testNorm, 2, 0.9, 0.05, 0.05, writer);

      System.out.println("====12 threads====");
      System.out.println("**step 1**");
      part3(testRand, testNorm, 12, 0.1, 0.1, 0.8, writer);
      System.out.println("**step 2**");
      part3(testRand, testNorm, 12, 0.5, 0.5, 0.0, writer);
      System.out.println("**step 3**");
      part3(testRand, testNorm, 12, 0.5, 0.25, 0.25, writer);
      System.out.println("**step 4**");
      part3(testRand, testNorm, 12, 0.9, 0.05, 0.05, writer);

      System.out.println("====30 threads====");
      System.out.println("**step 1**");
      part3(testRand, testNorm, 30, 0.1, 0.1, 0.8, writer);
      System.out.println("**step 2**");
      part3(testRand, testNorm, 30, 0.5, 0.5, 0.0, writer);
      System.out.println("**step 3**");
      part3(testRand, testNorm, 30, 0.5, 0.25, 0.25, writer);
      System.out.println("**step 4**");
      part3(testRand, testNorm, 30, 0.9, 0.05, 0.05, writer);

      System.out.println("====46 threads====");
      System.out.println("**step 1**");
      part3(testRand, testNorm, 46, 0.1, 0.1, 0.8, writer);
      System.out.println("**step 2**");
      part3(testRand, testNorm, 46, 0.5, 0.5, 0.0, writer);
      System.out.println("**step 3**");
      part3(testRand, testNorm, 46, 0.5, 0.25, 0.25, writer);
      System.out.println("**step 4**");
      part3(testRand, testNorm, 46, 0.9, 0.05, 0.05, writer);

      writer.close();
    } catch (Exception e) {
    }

  }


  private static void part3(TestSkipList rand, TestSkipList norm, int threads, double a, double r, double c, BufferedWriter w) {
    long executionTimeRand = 0;
    long executionTimeNorm = 0;
    long begin;

    for (int i = 0; i < NUM_TESTS; i++) {
      System.out.println("run "+i);
      rand.set(NUM_OPERATIONS, threads, a, r, c);
      begin = System.nanoTime();
      rand.start();
      executionTimeRand += (System.nanoTime() - begin);

      norm.set(NUM_OPERATIONS, threads, a, r, c);
      begin = System.nanoTime();
      norm.start();
      executionTimeNorm += (System.nanoTime() - begin);
    }
    executionTimeRand /= NUM_TESTS;
    executionTimeNorm /= NUM_TESTS;
    // System.out.println("Rand Execution Time (" + threads + " Threads | " + a + " " + r + " " + c + " ): " + executionTimeRand);
    // System.out.println("Norm Execution Time (" + threads + " Threads | " + a + " " + r + " " + c + " ): " + executionTimeNorm);
    try {
      w.write("Random," + threads + "," + a + "," + r + "," + c + "," + executionTimeRand + "\n");
      w.write("Normal," + threads + "," + a + "," + r + "," + c + "," + executionTimeNorm + "\n");
    } catch (Exception e) {}
  }
}
