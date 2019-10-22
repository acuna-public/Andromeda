  package pro.acuna.andromeda;
  /*
   Created by Acuna on 26.04.2018
  */
  
  public class Logger {
    
    private final String name;
    private int level;
    
    public Logger (String name, int level) {
      this.name = name;
      this.level = level;
    }
    
    public void setLevel (int level) {
      this.level = level;
    }
    
    private String addName (String msg) {
      return name + "| " + msg;
    }
    
    public void i (String msg) {
      if (level <= android.util.Log.INFO)
        Log.i (addName (msg));
    }
    
    public void i (String msg, Exception e) {
      if (level <= android.util.Log.INFO)
        Log.i (addName (msg), e);
    }
    
    public void w (String msg) {
      //if (level <= android.util.Log.WARN)
      //  Log.w (addName (msg));
    }
    
    public void w (String msg, Exception e) {
      if (level <= android.util.Log.WARN)
        Log.w (addName (msg), e);
    }
    
    public void e (String msg) {
      if (level <= android.util.Log.ERROR)
        Log.e (addName (msg));
    }
    
    public void e (String msg, Exception e) {
      if (level <= android.util.Log.ERROR)
        Log.e (addName (msg), e);
    }
    
    public void d (String msg) {
      if (level <= android.util.Log.DEBUG)
        Log.d (addName (msg));
    }
    
    public void d (String msg, Exception e) {
      if (level <= android.util.Log.DEBUG)
        Log.d (addName (msg), e);
    }
    
    public void v (String msg) {
      if (level <= android.util.Log.VERBOSE)
        Log.v (addName (msg));
    }
    
    public void v (String msg, Exception e) {
      if (level <= android.util.Log.VERBOSE)
        Log.v (addName (msg), e);
    }
    
  }