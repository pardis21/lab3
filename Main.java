import java.util.LinkedList;
import java.util.List;
import java.util.stream.*;
import java.util.concurrent.ForkJoinPool;

class Main {

  public static void main(String[] args) {
    final int MAX_THREADS = (args.length>1)?Integer.parseInt(args[1]):4;
    final int MODE = Integer.parseInt(args[0]);
    System.out.println("Number of Threads: "+MAX_THREADS);
    Sorting s;
    long start, end, duration;

    List<Integer> input = new LinkedList<Integer>();
    for (int i = 0; i < 1000000; i++) {
      input.add((int)(Math.random() * 100));
    }

    switch(MODE) {
      // #region Task 1: Sequential
      case 1:
        System.out.println("====================Task 1==============================");  
        s = new MergeSort(input);
        start = System.nanoTime();
        s.sort();
        end = System.nanoTime();
        duration = end - start;
        System.out.println("1:"+duration/1000000.0);
        break;
        // #endregion
      case 2:
        System.out.println("====================Task 2==============================");  
        // #region Task 2: Executer Service
        s = new MergeSortES(input, MAX_THREADS);
        start = System.nanoTime();
        s.sort();
        end = System.nanoTime();
        duration = end - start;
        System.out.println("2:"+duration/1000000.0);
        break;
        // #endregion
      case 3:
        System.out.println("====================Task 3==============================");  
        // #region Task 3: ForkJoinPool
        s = new MergeSortJF(input);
        start = System.nanoTime();
        s.sort();
        end = System.nanoTime();
        duration = end - start;
        System.out.println("3:"+duration/1000000.0);
        //#endregion
        break;
      case 4:
        System.out.println("====================Task 4==============================");  
        // #region Task 4: streams and lambda
        s = new MergeSortStream(input,MAX_THREADS);
        start = System.nanoTime();
        s.sort();
        end = System.nanoTime();
        duration = end - start;
        System.out.println("4:"+duration/1000000.0);
        break;
      default:
        break;
    }
  }
}

