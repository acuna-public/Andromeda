  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.content.Context;
  import android.os.StatFs;
  
  import java.io.IOException;
  
  import upl.core.File;
  import upl.core.exceptions.OutOfMemoryException;
  import upl.util.List;
  
  public class Files {
    
    public static void write (Context context, Object text, String fileName) throws IOException {
      write (context, text, fileName, false);
    }
    
    public static void write (Context context, Object text, String fileName, boolean append) throws IOException {
      OS.appFilesPath (context, fileName).write (text, append);
    }
    
    public static void write (Context context, List<String> items, String fileName) throws IOException {
      write (context, items, fileName, false);
    }
    
    public static void write (Context context, List<String> items, String fileName, boolean append) throws IOException {
	    OS.appFilesPath (context, fileName).write (items, append);
    }
    
    public static String read (Context context, String fileName) throws IOException, OutOfMemoryException {
      return OS.appFilesPath (context, fileName).read ();
    }
    
    public static List<String> read (Context context, String fileName, List<String> output) throws IOException, OutOfMemoryException {
      return OS.appFilesPath (context, fileName).read (output);
    }
    
    public static void delete (Context context, String fileName) throws IOException {
	    OS.appFilesPath (context, fileName).delete ();
    }
    
    public static boolean exists (Context context, String fileName) {
      return OS.appFilesPath (context, fileName).exists ();
    }
    
    /*public static File getExternalFilesDir (Context context, ApplicationInfo appInfo) {
      
      try {
        return getExternalFilesDir (context, appInfo.packageName);
      } catch (PackageManager.NameNotFoundException e) {
        return null;
      }
      
    }*/
    
    public static long getFolderSize (File dir) throws IOException {
      return getFolderSize (dir, null);
    }
    
    public static long getFolderSize (File dir, upl.core.File.CountListener listener) throws IOException {
      
      long size = 0;
      
      try {
        
        String strDir = dir.getAbsolutePath ();
        
        if (!strDir.equals ("/")) {
          
          StatFs stat = new StatFs (strDir);
          size = dir.getFolderSize (Device.getBlockSize (stat), listener);
          
        } else size = Device.getDeviceUsedSpace ();
        
      } catch (IllegalArgumentException e) {
        //
      }
      
      if (listener != null) listener.onFinish (size);
      
      return size;
      
    }
    
  }