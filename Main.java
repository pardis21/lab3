import java.util.*;
import java.io.*;

class Main {

  public static void main(String[] args) {

    final int LAB_VALUE = 100000;
    final int SIZE = (args.length > 0) ? Integer.parseInt(args[0]) : LAB_VALUE;

    // Part 2

    // random distribution
    SkipList slRand = new SkipList();
    final double MEAN = SIZE / 2.0;
    final double STD_DEV = SIZE / Math.sqrt(12.0);

    System.out.println("Estimated mean: " + MEAN);
    System.out.println("Estimated std deviation: " + STD_DEV);
    System.out.println("============================================");
    long begin = System.nanoTime();
    for (int i = 0; i < SIZE; i++) {
      double value = (Math.random() * SIZE);
      slRand.add((int) value);
    }
    System.out.println("duration: " + (System.nanoTime() - begin));
    slRand.plotValues("rand");
    // slRand.printList();
    slRand.printStats();

    // normal distribution
    SkipList slNorm = new SkipList();
    Random rand = new Random();
    begin = System.nanoTime();
    for (int i = 0; i < SIZE; i++) {
      double value = (rand.nextGaussian() * STD_DEV + MEAN);
      if (value > SIZE)
        value = SIZE;
      else if (value < 0)
        value = 0;
      slNorm.add((int) value);
    }
    System.out.println("duration: " + (System.nanoTime() - begin));
    slNorm.plotValues("norm");
    // slNorm.printList();
    slNorm.printStats();

    // Part 3
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("part3.csv"));

      // 2 threads
      part3(slRand, slNorm, 2, 0.1, 0.1, 0.8, writer);
      part3(slRand, slNorm, 2, 0.5, 0.5, 0.0, writer);
      part3(slRand, slNorm, 2, 0.5, 0.25, 0.25, writer);
      part3(slRand, slNorm, 2, 0.9, 0.05, 0.05, writer);

      // 12 threads
      part3(slRand, slNorm, 12, 0.1, 0.1, 0.8, writer);
      part3(slRand, slNorm, 12, 0.5, 0.5, 0.0, writer);
      part3(slRand, slNorm, 12, 0.5, 0.25, 0.25, writer);
      part3(slRand, slNorm, 12, 0.9, 0.05, 0.05, writer);

      // 30 threads
      part3(slRand, slNorm, 30, 0.1, 0.1, 0.8, writer);
      part3(slRand, slNorm, 30, 0.5, 0.5, 0.0, writer);
      part3(slRand, slNorm, 30, 0.5, 0.25, 0.25, writer);
      part3(slRand, slNorm, 30, 0.9, 0.05, 0.05, writer);

      // 46 threads
      part3(slRand, slNorm, 46, 0.1, 0.1, 0.8, writer);
      part3(slRand, slNorm, 46, 0.5, 0.5, 0.0, writer);
      part3(slRand, slNorm, 46, 0.5, 0.25, 0.25, writer);
      part3(slRand, slNorm, 46, 0.9, 0.05, 0.05, writer);

      writer.close();
    } catch (Exception e) {
    }
  }

  private static void part3(SkipList rand, SkipList norm, int threads, double a, double r, double c, BufferedWriter w) {
    long executionTimeRand = 0;
    long executionTimeNorm = 0;
    TestSkipList test;
    long begin;
    final int NUM_TESTS = 10;
    final int NUM_OPERATIONS = 100;

    for (int i = 0; i < NUM_TESTS; i++) {
      test = new TestSkipList(rand);
      test.set(NUM_OPERATIONS, threads, a, r, c);
      begin = System.nanoTime();
      test.start();
      test.stop();
      executionTimeRand += (System.nanoTime() - begin);

      test = new TestSkipList(norm);
      test.set(NUM_OPERATIONS, threads, a, r, c);
      begin = System.nanoTime();
      test.start();
      test.stop();
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
