  package pro.acuna.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.app.ActivityManager;
  import android.content.Context;
  import android.content.pm.PackageManager;
  import android.os.Process;
  
  import java.io.File;
  import java.io.IOException;
  import java.util.ArrayList;
  import java.util.List;
  
  import pro.acuna.jabadaba.Arrays;
  import pro.acuna.jabadaba.Console;
  import pro.acuna.jabadaba.Files;
  import pro.acuna.jabadaba.Int;
  
  public class System {
    
    public static boolean debug = false;
    
    public static List<ActivityManager.RunningAppProcessInfo> processList (Context context) {
      
      ActivityManager activityManager = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
      return (activityManager != null ? activityManager.getRunningAppProcesses () : new ArrayList<ActivityManager.RunningAppProcessInfo> ());
      
    }
    
    public static List<String> remount (String file, String mountType, List<String> cmds) throws SystemException {
      
      try {
        
        mountType = mountType.toLowerCase ();
        List<String[]> mountPoints = pro.acuna.jabadaba.System.getMountPoint (file);
        
        if (Int.size (mountPoints) > 0) {
          
          String[] mountPoint = mountPoints.get (0);
          
          if (!mountPoint[3].contains (mountType)) {
            
            String device = new File (mountPoint[0]).getAbsolutePath ();
            String point = new File (mountPoint[1]).getAbsolutePath ();
            
            cmds.add (Device.box ("mount -o remount," + mountType + " " + device + " " + point));
            cmds.add ("mount -o remount," + mountType + " " + device + " " + point);
            cmds.add ("mount -o remount," + mountType + " " + file);
            //cmds.add ("/system/bin/toolbox mount -o remount," + mountType + " " + device + " " + point);
            
            if (Device.box ().equals ("toybox"))
              cmds.add ("/system/bin/toybox mount -o remount," + mountType + " " + device + " " + point);
            
          }
          
        }
        
      } catch (Console.ConsoleException e) {
        throw new SystemException (e);
      }
      
      return cmds;
      
    }
    
    public static boolean remount (String file, String mountType) throws SystemException {
      
      try {
        
        Console exec = new Console ();
        
        //exec.showErrors = true;
        exec.shell (Console.su);
        
        List<String> cmds = remount (file, mountType, new ArrayList<String> ());
        List<String> output = exec.query (cmds);
        
        if (Int.size (output) > 0)
          throw new IOException ("Can't remount " + file + ": " + Arrays.implode (output));
        
        return pro.acuna.jabadaba.System.checkMountPoint (file);
        
      } catch (IOException | Console.ConsoleException e) {
        throw new SystemException (e);
      }
      
    }
    
    public static class SystemException extends Exception {
      
      private SystemException (Exception e) {
        super (e);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public static void killProcess (Context context, String appName) throws Console.ConsoleException, PackageManager.NameNotFoundException {
      killProcess (context, new String[] { appName });
    }
    
    public static void killProcess (Context context, String[] appNames) throws Console.ConsoleException, PackageManager.NameNotFoundException {
      
      PackageData pInfo;
      String[] names = new String[Int.size (appNames)];
      
      for (int i = 0; i < Int.size (appNames); ++i) {
        
        pInfo = new AppsManager (context).getPackageData (appNames[i]);
        names[i] = pInfo.appInfo.processName;
        
      }
      
      pro.acuna.jabadaba.System.killProcess (names, new ArrayList<String> ());
      
    }
    
    public static List<String> killProcess (PackageData pInfo, List<String> cmds) throws Console.ConsoleException {
      return pro.acuna.jabadaba.System.killProcess (pInfo.appInfo.processName, cmds);
    }
    
    public static String error (Context context, Exception e) {
      return error (context, e, false);
    }
    
    public static String error (Context context, Exception e, boolean full) {
      return error (context, e, "", full);
    }
    
    public static String error (Context context, Exception e, String appName) {
      return error (context, e, appName, "error");
    }
    
    public static String error (Context context, Exception e, String appName, String fileName) {
      return error (context, e, appName, fileName, false);
    }
    
    public static String error (Context context, Exception e, String appName, boolean full) {
      return error (context, e, appName, "error", full);
    }
    
    private static String logFile (Context context, String file) {
      return OS.getExternalFilesDir (context) + "/logs/" + file + ".log";
    }
    
    public static String error (Context context, Exception e, String appName, String file, boolean full) {
      
      Files.writeLog (e, logFile (context, file), appName);
      return (full ? e.toString () : e.getMessage ());
      
    }
    
    public static void writeLog (Context context, String text, String file) {
      pro.acuna.jabadaba.Files.writeLog (text, logFile (context, file));
    }
    
    public static String error (Context context, Throwable e) {
      return error (context, e, false);
    }
    
    public static String error (Context context, Throwable e, boolean full) {
      return error (context, e, "", full);
    }
    
    public static String error (Context context, Throwable e, Object appName) {
      return error (context, e, appName, "error", false);
    }
    
    public static String error (Context context, Throwable e, Object appName, boolean full) {
      return error (context, e, appName, "error", full);
    }
    
    public static String error (Context context, Throwable e, Object appName, String file, boolean full) {
      
      Files.writeLog (e, logFile (context, file), appName);
      return (full ? e.toString () : e.getMessage ());
      
    }
    
    public static String error (Context context, List<?> items) {
      return error (context, Arrays.implode (items));
    }
    
    public static String error (Context context, String mess) {
      return error (context, mess, "error");
    }
    
    public static String error (Context context, String mess, String file) {
      
      Files.writeLog (pro.acuna.jabadaba.Files.logText (mess), logFile (context, file));
      return mess;
      
    }
    
    public static void killProcess () {
      
      Process.killProcess (Process.myPid ());
      java.lang.System.exit (10);
      
    }
    
  }