import java.util.*;
import java.util.stream.*;

public class History {

  private List<Log> history;

  public History() {
    history = new LinkedList<Log>();
  }

  public void add(Log l) { history.add(l); }

  public void get(int i) { history.get(i); }
  
  public List<Log> list() { return history; }

  public String toString() { return history.toString(); }

  public boolean check() { 
      for(int i = 1; i < history.size(); i++) {
        if(history.get(i-1).timestamp > history.get(i).timestamp ){
          System.out.println("unordered list" + i+" "+history.get(i).timestamp);
          return false;
        }

        switch(history.get(i).method) {
          case CONTAINS:
            for(int c = i-1; c>= 0; c--) {
              if(history.get(c).value == history.get(i).value && history.get(c).success) {
                switch(history.get(c).method) {
                  case CONTAINS:
                    if(history.get(c).success != history.get(i).success) {
                      System.out.println("contains-contains "+i+" "+history.get(i).timestamp);
                      return false;
                    }
                    break;
                  case REMOVE:
                    if(history.get(c).success == history.get(i).success){
                      System.out.println("contains-remove "+i+" "+history.get(i).timestamp); 
                      return false;
                    }
                    break;
                  case ADD:
                    if(!history.get(i).success){
                      System.out.println("contains-add "+i+" "+history.get(i).timestamp);
                      return false;
                    }
                    break;
                }
              }
              break;
            }
            break;
          case REMOVE:
            int r;
            for(r=i-1; r >=0; r--) {
              if(history.get(r).value == history.get(i).value && history.get(r).success) {
                switch(history.get(r).method) {
                  case CONTAINS:
                    if(history.get(r).success != history.get(i).success){
                      System.out.println("remove-contains "+i+" "+history.get(i).timestamp);
                      return false;
                    }
                    break;
                  case REMOVE:
                    if(history.get(i).success) {
                      System.out.println("remove-remove "+i+" "+history.get(i).timestamp);
                      return false;
                    }
                    break;
                  case ADD:
                    if(history.get(i).success != history.get(r).success) {
                      System.out.println("remove-add "+i+" "+history.get(i).timestamp);
                      return false;
                    }
                    break;
                }
                break;
              }
            }
              if(r == 0 && history.get(i).success && history.get(r).method != Log.Method.ADD){
                System.out.println("successful removing of item never added "+i+" "+history.get(i).timestamp);
                return false;
              }
            break;
          case ADD:
            int a;
            for(a=i-1; a >= 0; a--) {
                if(history.get(a).value == history.get(i).value && history.get(a).success) {
                    switch(history.get(a).method) {
                      case CONTAINS:
                        if(history.get(a).success == history.get(i).success){
                          System.out.println("add-contains "+i+" "+history.get(i).timestamp);
                          return false;
                        }
                        break;
                      case REMOVE:
                        if(!history.get(i).success) {
                          System.out.println("add-remove "+i+" "+history.get(i).timestamp);
                          return false;
                        }
                        break;
                      case ADD:
                        if(history.get(i).success == history.get(a).success) {
                          System.out.println("add-add "+i+" "+history.get(i).timestamp);
                          return false;
                        }
                        break;
                    }
                    break;
                }
            }
            if (a==0 && !history.get(i).success && history.get(a).method != Log.Method.ADD){
                System.out.println("first add unsuccessful "+i+" "+history.get(i).timestamp);
                return false;
            }
            break;
        }

      }
    return true;
    }

  public static List<Log> merge (List<List<Log>> logs) {
    List<Log> tmp = logs.parallelStream()
      .flatMap(history -> history.parallelStream())
      .collect(Collectors.toList());

    List<Log> result = tmp.parallelStream()
      .sorted((o1, o2)-> o1.compareTo(o2))
      .collect(Collectors.toList());
    return result;

  }

  public History(List<Log> history) { this.history = history;}


}
