  package pro.acuna.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.content.Context;
  import android.os.StatFs;
  
  import java.io.File;
  import java.io.IOException;
  import java.util.ArrayList;
  import java.util.List;
  
  import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
  
  public class Files {
    
    public static void write (Context context, Object text, String fileName) throws IOException {
      write (context, text, fileName, false);
    }
    
    public static void write (Context context, Object text, String fileName, boolean append) throws IOException {
      pro.acuna.jabadaba.Files.write (text, OS.appFilesPath (context, fileName), append);
    }
    
    public static void write (Context context, List<String> items, String fileName) throws IOException {
      write (context, items, fileName, false);
    }
    
    public static void write (Context context, List<String> items, String fileName, boolean append) throws IOException {
      pro.acuna.jabadaba.Files.write (items, OS.appFilesPath (context, fileName), append);
    }
    
    public static String read (Context context, String fileName) throws IOException, OutOfMemoryException {
      return pro.acuna.jabadaba.Files.read (OS.appFilesPath (context, fileName));
    }
    
    public static List<String> read (Context context, String fileName, List<String> output) throws IOException {
      return pro.acuna.jabadaba.Files.read (OS.appFilesPath (context, fileName), output);
    }
    
    public static void delete (Context context, String fileName) throws IOException {
      pro.acuna.jabadaba.Files.delete (OS.appFilesPath (context, fileName));
    }
    
    public static boolean exists (Context context, String fileName) {
      return pro.acuna.jabadaba.Files.exists (OS.appFilesPath (context, fileName));
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
    
    public static long getFolderSize (File dir, pro.acuna.jabadaba.Files.CountListener listener) throws IOException {
      
      long size = 0;
      
      try {
        
        String strDir = dir.getAbsolutePath ();
        
        if (!strDir.equals ("/")) {
          
          StatFs stat = new StatFs (strDir);
          size = pro.acuna.jabadaba.Files.getFolderSize (dir, Device.getBlockSize (stat), listener);
          
        } else size = Device.getDeviceUsedSpace ();
        
      } catch (IllegalArgumentException e) {
        //
      }
      
      if (listener != null) listener.onFinish (size);
      
      return size;
      
    }
    
  }