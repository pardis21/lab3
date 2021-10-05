class Main {

  public static void main(String[] args) {
    System.out.println("Hello World");

    SkipList<Integer> sl = new SkipList<Integer>();
    sl.add(0);
    sl.add(10);
    sl.add(2);
    sl.add(7);
    sl.add(5);
    sl.add(10);

    sl.printList();
  }
}

