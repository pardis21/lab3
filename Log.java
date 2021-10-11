public class Log {
  
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

  public int equals(Log l) {
    if(this.timestamp > l.timestamp)
      return 1;
    else if (this.timestamp < l.timestamp)
      return -1;
    else 
      return 0;
  }

  public String toString() {
    return timestamp+" "+method+" "+value+"("+success+")\n";
  }

}
