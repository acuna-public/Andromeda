  package pro.acuna.andromeda;
  /*
    Created by Acuna on 26.04.2018
  */
  
  import java.util.Formatter;
  
  import pro.acuna.jabadaba.System;
  
  public class Log {
    
    public static final boolean ENABLE_DEBUG = false;
    
    private static final String TAG = "andromeda";
    
    private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter> () {
      
      protected ReusableFormatter initialValue () {
        return new ReusableFormatter ();
      }
      
    };
    
    public static void d (Throwable err, String msg, Object... args) {
      android.util.Log.d (TAG, pro.acuna.andromeda.Log.format (msg, args), err);
    }
    
    public static void i (Object... msg) {
      android.util.Log.i (TAG, System.debug (msg));
    }
    
    public static void i (Object msg, Exception e) {
      android.util.Log.i (TAG, System.debug (msg), e);
    }
    
    public static void i (Throwable err, String msg, Object... args) {
      android.util.Log.i (TAG, pro.acuna.andromeda.Log.format (msg, args), err);
    }
    
    public static void w (Object... msg) {
      android.util.Log.w (TAG, System.debug (msg));
    }
    
    public static void w (Object msg, Exception e) {
      android.util.Log.w (TAG, System.debug (msg), e);
    }
    
    public static void w (Throwable err, String msg, Object... args) {
      android.util.Log.w (TAG, pro.acuna.andromeda.Log.format (msg, args), err);
    }
    
    public static void e (Object... msg) {
      android.util.Log.e (TAG, System.debug (msg));
    }
    
    public static void e (Object msg, Exception e) {
      android.util.Log.e (TAG, System.debug (msg), e);
    }
    
    public static void e (Throwable err, String msg, Object... args) {
      android.util.Log.e (TAG, pro.acuna.andromeda.Log.format (msg, args), err);
    }
    
    public static void d (Object... msg) {
      android.util.Log.d (TAG, System.debug (msg));
    }
    
    public static void d (Object msg, Exception e) {
      android.util.Log.d (TAG, System.debug (msg), e);
    }
    
    public static void v (Object... msg) {
      android.util.Log.v (TAG, System.debug (msg));
    }
    
    public static void v (Object msg, Exception e) {
      android.util.Log.v (TAG, System.debug (msg), e);
    }
    
    public static Logger create (String name) {
      return create (name, android.util.Log.VERBOSE);
    }
    
    public static Logger create (String name, int level) {
      return new Logger (name, level);
    }
    
    public static String format (String msg, Object... args) {
      
      ReusableFormatter formatter = thread_local_formatter.get ();
      return formatter.format (msg, args);
      
    }
    
    /**
     A little trick to reuse a formatter in the same thread
     */
    private static class ReusableFormatter {
      
      private Formatter formatter;
      private StringBuilder builder;
      
      ReusableFormatter () {
        
        builder = new StringBuilder ();
        formatter = new Formatter (builder);
        
      }
      
      public String format (String msg, Object... args) {
        
        formatter.format (msg, args);
        
        String s = builder.toString ();
        builder.setLength (0);
        
        return s;
        
      }
      
    }
    
  }