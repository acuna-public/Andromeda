  package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 27.07.2017
  */
  
  import android.annotation.TargetApi;
  import android.app.Activity;
  import android.content.Context;
  import android.content.pm.ActivityInfo;
  import android.content.res.Configuration;
  import android.net.ConnectivityManager;
  import android.net.NetworkInfo;
  import android.os.Build;
  import android.os.Environment;
  import android.os.StatFs;
  import android.util.DisplayMetrics;
  import android.view.Display;
  import android.view.Surface;
  import android.view.View;
  import android.view.Window;
  import android.view.WindowManager;
  
  import java.io.BufferedReader;
  import java.io.File;
  import java.io.FileReader;
  import java.io.IOException;
  import java.io.InputStreamReader;
  import java.lang.reflect.InvocationTargetException;
  import java.lang.reflect.Method;
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.LinkedHashMap;
  import java.util.List;
  import java.util.Map;
  import java.util.Objects;

  import ru.ointeractive.andromeda.graphic.Graphic;
  import upl.core.Arrays;
  import upl.core.Console;
  import upl.core.Int;
  import upl.core.System;
  import upl.core.exceptions.ConsoleException;

  public class Device {
    
    public static Map<String, String> getCPUInfo () throws IOException {
      
      Map<String, String> output = new HashMap<> ();
      
      BufferedReader br = new BufferedReader (new FileReader ("/proc/cpuinfo"));
      
      String str;
      
      while ((str = br.readLine ()) != null) {
        
        String[] data = str.split (":");
        
        if (Int.size (data) > 1) {
          
          String key = data[0].trim ().replace (" ", "_");
          if (key.equals ("model_name")) key = "cpu_model";
          
          String value = data[1].trim ();
          
          if (key.equals ("cpu_model"))
            value = value.replaceAll ("\\s+", " ");
          
          output.put (key, value);
          
        }
        
      }
      
      br.close ();
      
      return output;
      
    }
    
    //@SuppressWarnings ("deprecation")
    public static Map<String, Object> getInfo () {
      
      Map<String, Object> info = new LinkedHashMap<> ();
      
      info.put ("board", Build.BOARD);
      info.put ("bootloader", Build.BOOTLOADER);
      info.put ("brand", new upl.type.String (Build.BRAND).ucfirst ());
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        info.put ("cpu_type", Arrays.implode (", ", Build.SUPPORTED_ABIS));
      else
        info.put ("cpu_type", Arrays.implode (", ", new String[] {Build.CPU_ABI, Build.CPU_ABI2}));
      
      info.put ("cpu_arch", Objects.requireNonNull (java.lang.System.getProperty ("os.arch")));
      
      info.put ("device", Build.DEVICE);
      info.put ("display", Build.DISPLAY);
      info.put ("hardware", Build.HARDWARE);
      info.put ("host", Build.HOST);
      info.put ("id", Build.ID);
      info.put ("manufacturer", new upl.type.String (Build.MANUFACTURER).ucfirst ());
      info.put ("model", Build.MODEL);
      info.put ("product", Build.PRODUCT);
      //info.put ("radio", Build.getRadioVersion ());
      info.put ("release", Build.VERSION.RELEASE);
	    info.put ("fingerprint", Build.FINGERPRINT);
      
      info.put ("sdk", Build.VERSION.SDK_INT);
      
      if (Build.VERSION.SDK_INT >= 23) {
        
        info.put ("prev_sdk", Build.VERSION.PREVIEW_SDK_INT);
        info.put ("security_path", Build.VERSION.SECURITY_PATCH);
        
      }
      
      info.put ("tags", Build.TAGS);
      info.put ("type", Build.TYPE);
      info.put ("user", Build.USER);
      
      if (OS.releasesNames ().get (info.get ("release")) != null)
        info.put ("version_name", OS.releasesNames ().get (info.get ("release")));
      else
        info.put ("version_name", "");
      
      return info;
      
    }
    
    public static class NoRootException extends Exception {
      
      public NoRootException (String msg) {
        super (msg);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public static boolean isRoot () {
      
      boolean output;
      
      String buildTags = Build.TAGS;
      output = (buildTags != null && buildTags.contains ("test-keys"));
      
      output = (!output && (new File ("/system/app/Superuser.apk").exists () || System.findBinary (Console.su).size () > 0));
      
      if (!output) {
        
        Process process = null;
        
        try {
          
          process = Runtime.getRuntime ().exec (new String[] {"/system/xbin/which", Console.su});
          BufferedReader in = new BufferedReader (new InputStreamReader (process.getInputStream ()));
          output = (in.readLine () != null);
          
        } catch (IOException e) {
          // empty
        } finally {
          if (process != null) process.destroy ();
        }
        
      }
      
      return output;
      
    }
    
    public static List<String> reboot () throws ConsoleException {
      
      Console exec = new Console ();
      
      exec.shell (Console.su);
      //exec.sleep = 5000;
      
      return exec.query (new String[] {
      	
	      "am broadcast -a android.intent.action.ACTION_SHUTDOWN",
	      "sleep 5",
	      "reboot",
				
      });
      
    }
    
    public static List<String> quickReboot () throws ConsoleException {
      return System.exec (Console.su, "pkill system_server");
    }
    
    public static String box () {
      
      if (Build.VERSION.SDK_INT >= 23)
        return "toybox";
      else
        return "busybox";
      
    }
    
    public static String box (String cmd) {
      return box () + " " + cmd;
    }
    
    public static boolean checkBox () throws ConsoleException {
      
      final List<String> errors = new ArrayList<> ();
      
      Console exec = new Console (new Console.Listener () {
        
        @Override
        public void onExecute (String line, int i) {
        }
        
        @Override
        public void onSuccess (String line, int i) {
        }
        
        @Override
        public void onError (String line, int i) {
          errors.add (line);
        }
        
      });
      
      exec.query (box ());
      
      return (Int.size (errors) == 0);
      
    }
    
    public static Object getServiceInfo (Context context, String service) {
      return context.getApplicationContext ().getSystemService (service);
    }
      
      /*public static SupplicantState getWiFiState (Context context) {
        
        WifiManager wifiManager = (WifiManager) getServiceInfo (context, Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo ();
        
        return wifiInfo.getSupplicantState ();
        
      }*/
    
    public static NetworkInfo getNetworkInfo (Context context) {
      
      ConnectivityManager connManager = (ConnectivityManager) context.getSystemService (Context.CONNECTIVITY_SERVICE);
      return (connManager != null ? connManager.getActiveNetworkInfo () : null);
      
    }
    
    public static boolean isOnline (Context context) {
      return (isConnected (getNetworkInfo (context)));
    }
    
    public static boolean isConnected (Context context) {
      return isConnected (getNetworkInfo (context));
    }
    
    public static boolean isConnected (NetworkInfo networkInfo) {
      return (networkInfo != null && networkInfo.isConnected ());
    }
    
    public static boolean isOnline (Context context, boolean wiFiOnly) {
      return isOnline (getNetworkInfo (context), wiFiOnly);
    }
    
    public static boolean isOnline (NetworkInfo networkInfo, boolean wiFiOnly) {
      
      return (
        networkInfo != null && (
          isWiFiConnected (networkInfo, wiFiOnly)
            ||
            (!wiFiOnly && isConnected (networkInfo))
        )
      );
      
    }
    
    public static boolean isWiFiConnected (ConnectivityManager cm, boolean wiFiOnly) {
      
      NetworkInfo activeNetwork = cm.getActiveNetworkInfo ();
      return isWiFiConnected (activeNetwork, wiFiOnly);
      
    }
    
    public static boolean isWiFiConnected (NetworkInfo networkInfo, boolean wiFiOnly) {
      return (wiFiOnly && networkInfo.getType () == ConnectivityManager.TYPE_WIFI);
    }
    
    public static boolean isMobileConnected (NetworkInfo networkInfo, boolean wiFiOnly) {
      return (!wiFiOnly && isConnected (networkInfo));
    }
    
    public static boolean isConnected (Context context, int type) {
      
      NetworkInfo networkInfo = getNetworkInfo (context);
      return (networkInfo != null && networkInfo.getType () == type);
      
    }
    
    public static boolean isWiFiConnected (Context context) {
      return isConnected (context, ConnectivityManager.TYPE_WIFI);
    }
    
    public static boolean isMobileConnected (Context context) {
      return isConnected (context, ConnectivityManager.TYPE_MOBILE);
    }
    
    public static DisplayMetrics getScreenSize (Context context) {
      return context.getResources ().getDisplayMetrics ();
    }
    
    public static int getScreenWidth (Context context) {
      return getScreenSize (context).widthPixels;
    }
    
    public static int getScreenHeight (Context context) {
      return getScreenSize (context).heightPixels;
    }
    
    public static Display getDisplay (Context context) {
      
      WindowManager manager = (WindowManager) context.getSystemService (Context.WINDOW_SERVICE);
      return (manager != null ? manager.getDefaultDisplay () : null);
      
    }
    
    public static DisplayMetrics getDisplayMetrics (Context context) {
      return context.getResources ().getDisplayMetrics ();
    }
    
    public static float getScreenDensity (Context context) {
      return getDisplayMetrics (context).density;
    }
    
    public static int getScreenDensityDPI (Context context) {
      return Graphic.getDensityDpi (getDisplayMetrics (context).density);
    }
    
    public static float getScreenDensityWidth (Context context) {
      return getDisplayMetrics (context).xdpi;
    }
    
    public static float getScreenDensityHeight (Context context) {
      return getDisplayMetrics (context).ydpi;
    }
    
    public static float toPx (Context context, float dp) {
      return (DisplayMetrics.DENSITY_DEFAULT * getScreenDensityDPI (context) / dp);
    }
    
    public static float toDp (Context context, float px) {
      return px / (getScreenDensityDPI (context) / DisplayMetrics.DENSITY_DEFAULT);
    }
    
    public static int getScreenDPI (Context context) {
      
      DisplayMetrics metrics = getDisplayMetrics (context);
      return (int) (DisplayMetrics.DENSITY_DEFAULT * metrics.density);
      
    }
    
    public static int widthColsNum (Context context, int width) {
      
      DisplayMetrics displayMetrics = getDisplayMetrics (context);
      float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
      
      return (int) (dpWidth / width);
      
    }
    
    public static int getOrientation (Context context) {
      return context.getResources ().getConfiguration ().orientation;
    }
    
    public int getScreenRotation (Context context) {
      
      int rotation = getOrientation (context);
      
      DisplayMetrics dm = getDisplayMetrics (context);
      
      int width = dm.widthPixels;
      int height = dm.heightPixels;
      
      int orientation;
      
      if (
        
        (rotation == Surface.ROTATION_0
           ||
           rotation == Surface.ROTATION_180)
          && height > width ||
          (rotation == Surface.ROTATION_90
             || rotation == Surface.ROTATION_270)
            && width > height
      
      ) { // Портрет
        
        switch (rotation) {
          
          case Surface.ROTATION_0:
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            break;
          
          case Surface.ROTATION_90:
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            break;
          
          case Surface.ROTATION_180:
            orientation = (OS.SDK >= 9 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
          
          case Surface.ROTATION_270:
            orientation = (OS.SDK >= 9 ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
          
          default:
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            break;
          
        }
        
      } else {
        
        switch (rotation) {
          
          case Surface.ROTATION_0:
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            break;
          
          case Surface.ROTATION_90:
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            break;
          
          case Surface.ROTATION_180:
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            break;
          
          case Surface.ROTATION_270:
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            break;
          
          default:
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            break;
          
        }
        
      }
      
      return orientation;
      
    }
    
    public static boolean isPortrait (Context context) {
      
      int orientation = getOrientation (context);
      
      return (orientation == Configuration.ORIENTATION_PORTRAIT || orientation == Configuration.ORIENTATION_UNDEFINED || orientation == Configuration.ORIENTATION_SQUARE);
      
    }
    
    public static boolean isLandscape (Context context) {
      
      int orientation = getOrientation (context);
      return (orientation == Configuration.ORIENTATION_LANDSCAPE);
      
    }
    
    public static boolean isReverseLandscape (Context context) {
      
      int orientation = getOrientation (context);
      return (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
      
    }
    
    private static final boolean isApi18 = (Build.VERSION.SDK_INT >= 18);
    
    @TargetApi (18)
    private static long getBlockCount (StatFs stat) {
      return (isApi18 ? stat.getBlockCountLong () : stat.getBlockCount ());
    }
    
    @TargetApi (18)
    private static long getAvailableBlocks (StatFs stat) {
      return (isApi18 ? stat.getAvailableBlocksLong () : stat.getAvailableBlocks ());
    }
    
    @TargetApi (18)
    public static long getBlockSize (StatFs stat) {
      return (isApi18 ? stat.getBlockSizeLong () : stat.getBlockSize ());
    }
    
    @TargetApi (18)
    private static long getSpace (StatFs stat, long size) {
      return size * getBlockSize (stat);
    }
    
    public static long getFreeSpace (StatFs stat) {
      return getSpace (stat, getAvailableBlocks (stat));
    }
    
    public static long getDeviceFreeSpace () {
      
      File path = Environment.getDataDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getFreeSpace (stat);
      
    }
    
    public static long getUsedSpace (StatFs stat) {
      return getSpace (stat, getBlockCount (stat) - getAvailableBlocks (stat));
    }
    
    public static long getTotalSpace (StatFs stat) {
      return getSpace (stat, getBlockCount (stat));
    }
    
    public static long getDeviceUsedSpace () throws IllegalArgumentException {
      
      File path = Environment.getDataDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getUsedSpace (stat);
      
    }
    
    public static long getDeviceTotalSpace () throws IllegalArgumentException {
      
      File path = Environment.getDataDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getTotalSpace (stat);
      
    }
    
    public static long getExtSDCardFreeSpace () throws IllegalArgumentException {
      
      File path = Environment.getExternalStorageDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getFreeSpace (stat);
      
    }
    
    public static long getExtSDCardUsedSpace () throws IllegalArgumentException {
      
      File path = Environment.getExternalStorageDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getUsedSpace (stat);
      
    }
    
    public static long getExtSDCardTotalSpace () throws IllegalArgumentException {
      
      File path = Environment.getExternalStorageDirectory ();
      StatFs stat = new StatFs (path.getPath ());
      
      return getTotalSpace (stat);
      
    }
      
      /*public final void setScreenBrightness (Context context, int percent) {
        
        if (percent < 1)
          percent = 1;
        else if (percent > 100)
          percent = 100;
        
        final float level;
        final Integer oldColorLevel = myColorLevel;
        
        if (percent >= 25) {
          
          // 100 => 1f; 25 => .01f
          level = .01f + (percent - 25) * .99f / 75;
          myColorLevel = null;
          
        } else {
          
          level = .01f;
          myColorLevel = 0x60 + (0xFF - 0x60) * Math.max(percent, 0) / 25;
          
        }
        
      }*/
    
    private static final int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
    
    public static void applyScreenOrientation (Window wnd) {
      
      if (wnd != null) {
        
        WindowManager.LayoutParams attrs = wnd.getAttributes ();
        
        attrs.screenOrientation = screenOrientation;
        wnd.setAttributes (attrs);
          
          /*if (DeviceInfo.EINK_SCREEN) {
            //TODO:
            //EinkScreen.ResetController (mReaderView);
          }*/
        
      }
      
    }
    
    // support pre API LEVEL 9
    final static public int ActivityInfo_SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
    final static public int ActivityInfo_SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    final static public int ActivityInfo_SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
    final static public int ActivityInfo_SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
    final static public int ActivityInfo_SCREEN_ORIENTATION_FULL_SENSOR = 10;
    
    public static void setScreenOrientation (Activity activity, int angle) {
      
      int newOrientation = 0;
      boolean level9 = false;
      
      switch (angle) {
        
        case 0:
          newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; // level9 ? ActivityInfo_SCREEN_ORIENTATION_SENSOR_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        
        case 1:
          newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; // level9 ? ActivityInfo_SCREEN_ORIENTATION_SENSOR_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        
        case 2:
          newOrientation = level9 ? ActivityInfo_SCREEN_ORIENTATION_REVERSE_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        
        case 3:
          newOrientation = level9 ? ActivityInfo_SCREEN_ORIENTATION_REVERSE_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        
        case 4:
          newOrientation = level9 ? ActivityInfo_SCREEN_ORIENTATION_FULL_SENSOR : ActivityInfo.SCREEN_ORIENTATION_SENSOR;
          break;
        
        case 5:
          newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
          break;
        
      }
      
      if (newOrientation != screenOrientation) {
        
        activity.setRequestedOrientation (screenOrientation);
        applyScreenOrientation (activity.getWindow ());
        
      }
      
    }
    
    public static void applyFullScreen (Activity activity) {
      
      Window window = activity.getWindow ();
      
      window.setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      
    }
    
    public static void disallowFullScreen (Activity activity) {
      
      Window window = activity.getWindow ();
      
      window.setFlags (0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      
      setSystemUiVisibility (activity);
      
    }
    
    public void setKeyBacklight (Activity activity, int value) {
      
      setSystemUiVisibility (activity);
      
      // thread safe
      //return Engine.getInstance(this).setKeyBacklight(value);
      
    }
    
    public static void setSystemUiVisibility (Activity activity) {
      
      int flags = 0;
      flags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
      
      setSystemUiVisibility (activity, flags);
      
    }
    
    public static int getSDK () {
      return Build.VERSION.SDK_INT;
    }
    
    private static int lastSystemUiVisibility = -1;
    
    public static boolean setSystemUiVisibility (Activity activity, int value) {
      
      if (getSDK () < 19) {
        
        if (value == lastSystemUiVisibility && value != View.SYSTEM_UI_FLAG_LOW_PROFILE) return false;
        
        lastSystemUiVisibility = value;
        
        View view;
        
        view = activity.getWindow ().getDecorView ();
        
        if (view != null) {
          
          try {
            
            Method m = view.getClass ().getMethod ("setSystemUiVisibility", int.class);
            m.invoke (view, value);
            
            return true;
            
          } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            // ignore
          }
          
        }
        
      }
      
      return false;
      
    }
    
    public static int getTapZone (int x, int y, int dx, int dy) {
      
      int x1 = dx / 3;
      int x2 = dx * 2 / 3;
      int y1 = dy / 3;
      int y2 = dy * 2 / 3;
      
      int zone;
      
      if (y < y1) {
        
        if (x < x1) zone = 1;
        else if (x < x2) zone = 2;
        else zone = 3;
        
      } else if (y < y2) {
        
        if (x < x1) zone = 4;
        else if (x < x2) zone = 5;
        else zone = 6;
        
      } else {
        
        if (x < x1) zone = 7;
        else if (x < x2) zone = 8;
        else zone = 9;
        
      }
      
      return zone;
      
    }
    
  }