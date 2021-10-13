public class Log implements Comparable {
  
  public enum Method {
    ADD, REMOVE, CONTAINS;
  }

  public Method method;
  public int value;
  public boolean success;
  public long timestamp;

  public Log(Method m,boolean success ,int value, long ts) {
    method = m;
    this.value = value;
    this.success = success;
    timestamp = ts;
  }

  public int compareTo(Object l){
    if(this.timestamp < ((Log) l).timestamp)
      return -1;
    if(this.timestamp > ((Log) l).timestamp)
      return 1;
    if(this.timestamp == ((Log)l).timestamp)
      return 0;
    return 0;
  }


  public String toString() {
    return timestamp+" "+method+" "+value+"("+success+")\n";
  }

}
