import java.util.*;
import java.io.*;
import java.util.concurrent.*;

class Main {

  final static int LAB_VALUE = 1000000;
  final static int NUM_TESTS = 10;
  final static int NUM_OPERATIONS = 1000000;
  public static void main(String[] args) {

    final int SIZE = (args.length > 0) ? Integer.parseInt(args[0]) : LAB_VALUE;

    SkipList slRand = new SkipList();
    SkipList slNorm = new SkipList();
    Random rand = new Random();
    
    TestSkipList testRand = new TestSkipList(slRand, TestSkipList.Mode.RANDOM);
    TestSkipList testNorm = new TestSkipList(slNorm, TestSkipList.Mode.NORMAL);

    //System.out.println("Populating the lists...");
    // testRand.populate(SIZE);
    // testNorm.populate(SIZE);
  

   try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("part9.csv"));
      
      long begin = System.nanoTime();
      //System.out.println("====46 threads====");
      part9(testRand, testNorm, 46, 0.5, 0.5, 0.0, writer);
      System.out.println("Both list populated in: " + (System.nanoTime() - begin)/1000000000 + "s");

      System.out.println(testRand.getHistory());
      System.out.println(testNorm.getHistory());

      writer.close();
    } catch (Exception e) {
    }
  }


  private static void part9(TestSkipList rand, TestSkipList norm, int threads, double a, double r, double c, BufferedWriter w) {
    long executionTimeRand = 0;
    long executionTimeNorm = 0;
    long begin;

    for (int i = 0; i < NUM_TESTS; i++) {
      //System.out.println("run "+i);
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
