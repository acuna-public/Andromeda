  package ru.ointeractive.andromeda.apps;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.app.Activity;
  import android.app.ActivityManager;
  import android.app.admin.DevicePolicyManager;
  import android.content.ComponentName;
  import android.content.Context;
  import android.content.Intent;
  import android.content.pm.ApplicationInfo;
  import android.content.pm.PackageInfo;
  import android.content.pm.PackageManager;
  import android.content.res.Configuration;
  import android.content.res.Resources;
  import android.net.Uri;
  import android.os.Build;
  import android.webkit.MimeTypeMap;
  
  import java.io.IOException;
  import java.net.MalformedURLException;
  import java.net.URL;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Locale;

  import ru.ointeractive.andromeda.Device;
  import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.andromeda.System;
  import upl.core.Arrays;
  import upl.core.Console;
  import upl.core.File;
  import upl.core.Int;
  import upl.core.exceptions.ConsoleException;
  import upl.core.exceptions.OutOfMemoryException;

  public class AppsManager {
    
    Context context;
    
    public PackageManager pm;
    
    public AppsManager (Context context) {
      
      this.context = context;
      pm = context.getPackageManager ();
      
    }
    
    public PackageInfo getPackageInfo (String packageName) throws PackageManager.NameNotFoundException {
      return getPackageInfo (packageName, 0);
    }
    
    public PackageInfo getPackageInfo (String packageName, int flags) throws PackageManager.NameNotFoundException {
      return pm.getPackageInfo (packageName, flags);
    }
    
    public ApplicationInfo getAppInfo () {
      
      try {
        return getAppInfo (context.getPackageName ());
      } catch (PackageManager.NameNotFoundException e) {
        return null;
      }
      
    }
    
    public ApplicationInfo getAppInfo (String appName) throws PackageManager.NameNotFoundException {
      return getAppInfo (appName, 0);
    }
    
    public ApplicationInfo getAppInfo (String appName, int flags) throws PackageManager.NameNotFoundException {
      return pm.getApplicationInfo (appName, flags);
    }
    
    public PackageData getPackageData () {
      return getPackageData (0);
    }
    
    public PackageData getPackageData (int type) {
      
      try {
        return getPackageData (context.getPackageName (), type);
      } catch (PackageManager.NameNotFoundException e) {
        return null;
      }
      
    }
    
    public PackageData getPackageData (String id) throws PackageManager.NameNotFoundException {
      return getPackageData (id, 0);
    }
    
    public PackageData getPackageData (String id, int options) throws PackageManager.NameNotFoundException {
      return getPackageData (id, options, 0);
    }
    
    public PackageData getPackageData (String id, int options, int flags) throws PackageManager.NameNotFoundException {
      
      PackageInfo appInfo = getPackageInfo (id, flags);
      return getPackageData (appInfo, options);
      
    }
    
    public PackageData getPackageData (Uri uri) throws PackageManager.NameNotFoundException, IOException {
      return getPackageData (uri.getPath ());
    }
    
    public PackageData getPackageData (File packageFile) throws PackageManager.NameNotFoundException, IOException {
      return getPackageData (packageFile, 0);
    }
    
    public PackageData getPackageData (File packageFile, int options) throws PackageManager.NameNotFoundException, IOException {
      return getPackageData (packageFile, options, 0);
    }
    
    public PackageData getPackageData (File packageFile, int options, int flags) throws PackageManager.NameNotFoundException, IOException {
      
      PackageInfo pInfo = getPackageInfo (packageFile, flags);
      return getPackageData (pInfo, options);
      
    }
    
    public PackageData getPackageData (PackageInfo pInfo) {
      return getPackageData (pInfo, 0);
    }
    
    public PackageData getPackageData (PackageInfo pInfo, int type) {
      return new PackageData (this, pInfo, type);
    }
    
    public PackageInfo getPackageInfo (File packageFile) throws PackageManager.NameNotFoundException, IOException {
      return getPackageInfo (packageFile, 0);
    }
    
    public PackageInfo getPackageInfo (File packageFile, int flags) throws PackageManager.NameNotFoundException, IOException {
      
      if (packageFile.exists ()) {
        
        PackageInfo pInfo = pm.getPackageArchiveInfo (packageFile.getAbsolutePath (), flags);
        
        if (pInfo == null)
          throw new PackageManager.NameNotFoundException (packageFile.getAbsolutePath ());
        else
          return pInfo;
        
      } else throw new IOException ("File not exists: " + packageFile.getAbsolutePath ());
      
    }
    
    public void install (String file) {
      install (new File (file));
    }
    
    public void install (File file) {
      install (Uri.fromFile (file));
    }
    
    public void install (Uri file) {
      
      Intent intent = new Intent (Intent.ACTION_VIEW);
      
      MimeTypeMap myMime = MimeTypeMap.getSingleton ();
      String mimeType = myMime.getMimeTypeFromExtension ("apk");
      
      intent.setDataAndType (file, mimeType);
      intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
      
      //intent.putExtra (Intent.EXTRA_RETURN_RESULT, true);
      
      context.startActivity (intent);
      
    }
    
    public void uninstall (String id) {
      delete (context, id, Intent.ACTION_UNINSTALL_PACKAGE);
    }
    
    public static void uninstall (Context context, String id) {
      delete (context, id, Intent.ACTION_UNINSTALL_PACKAGE);
    }
    
    public void restore (String id) {
      delete (context, id, Intent.ACTION_DELETE);
    }
    
    public static void restore (Context context, String id) {
      delete (context, id, Intent.ACTION_DELETE);
    }
    
    public static void delete (Context context, String id, String action) {
      
      Intent intent = new Intent (action, Uri.parse ("package:" + id));
      
      //intent.putExtra (Intent.EXTRA_RETURN_RESULT, true);
      intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
      
      context.startActivity (intent);
      
    }
    
    String getLocalizedLabel (String lang, String packageName, int label, String title) throws PackageManager.NameNotFoundException {
      
      Configuration config = new Configuration ();
      
      config.locale = new Locale (lang);
      
      Resources res = pm.getResourcesForApplication (packageName);
      res.updateConfiguration (config, context.getResources ().getDisplayMetrics ());
      
      try {
        return new upl.type.String (res.getString (label)).ucfirst ();
      } catch (Resources.NotFoundException e) {
        return new upl.type.String (title).ucfirst ();
      }
      
    }
    
    public static final int GET_LABEL = 1;
    
    public List<PackageInfo> getInstalledPackages () {
      return getInstalledPackages (0);
    }
    
    public List<PackageInfo> getInstalledPackages (int flags) {
      return pm.getInstalledPackages (flags);
    }
    
    public List<ApplicationInfo> getInstalledApps () {
      return getInstalledApps (0);
    }
    
    public List<ApplicationInfo> getInstalledApps (int flags) {
      return pm.getInstalledApplications (flags);
    }
    
    public boolean isInstalled (String appName) {
      
      try {
        
        getPackageInfo (appName);
        return true;
        
      } catch (PackageManager.NameNotFoundException e) {
        return false;
      }
      
    }
    
    public boolean isSystem () throws PackageManager.NameNotFoundException {
      return isSystem (context.getPackageName ());
    }
    
    public boolean isSystem (String appName) throws PackageManager.NameNotFoundException {
      
      PackageInfo pInfo = getPackageInfo (appName);
      return isSystem (pInfo);
      
    }
    
    public boolean isSystem (PackageInfo pInfo) {
      return isSystem (pInfo.applicationInfo);
    }
    
    public boolean isSystem (ApplicationInfo pInfo) {
      return ((pInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
    
    public String[] getPermissions (PackageData pData) throws PackageManager.NameNotFoundException {
      return getPermissions (pData.packageName);
    }
    
    public String[] getPermissions () {
      
      try {
        return getPermissions (context.getPackageName ());
      } catch (PackageManager.NameNotFoundException e) {
        return new String[0];
      }
      
    }
    
    public String[] getPermissions (String packageName) throws PackageManager.NameNotFoundException {
      
      String[] perms = getPackageInfo (packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
      return (perms != null ? perms : new String[0]);
      
    }
    
    public String[] getPermissions (File packageName) throws PackageManager.NameNotFoundException, IOException {
      
      String[] perms = getPackageInfo (packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
      return (perms != null ? perms : new String[0]);
      
    }
    
    public void close () {
      close (context);
    }
    
    public static void close (Context context) {
      
      Intent intent = new Intent (Intent.ACTION_MAIN);
      
      intent.addCategory (Intent.CATEGORY_HOME);
      
      intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
      
      context.startActivity (intent);
      
    }
    
    public void changeLang (String lang) {
      
      Resources res = context.getResources ();
      Configuration conf = res.getConfiguration ();
      
      if (Build.VERSION.SDK_INT >= 17) {
        
        conf.setLocale (new Locale (lang));
        context.createConfigurationContext (conf);
        
      } else {
        
        conf.locale = new Locale (lang);
        res.updateConfiguration (conf, res.getDisplayMetrics ());
        
      }
      
    }
    
    public boolean isDeviceOwner (String packageName) {
      
      if (Build.VERSION.SDK_INT >= 18) {
        
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService (Context.DEVICE_POLICY_SERVICE);
        return (manager != null && manager.isDeviceOwnerApp (packageName));
        
      } else return false;
      
    }
    
    public boolean hasPermission (String permission) {
      return hasPermission (context.getPackageName (), permission);
    }
    
    public boolean hasPermission (String id, String permission) {
      return (pm.checkPermission (permission, id) == PackageManager.PERMISSION_GRANTED);
    }
    
    public ActivityManager activityManager () {
      return (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
    }
    
    public String systemPath () {
      
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
        return "/system/app";
      else
        return "/system/priv-app";
      
    }
    
    public String userPath () {
      return "/data/app";
    }
    
    public boolean isSelf (String appName) throws PackageManager.NameNotFoundException {
      
      PackageData appInfo = getPackageData (appName);
      return isSelf (appInfo);
      
    }
    
    public boolean isSelf (PackageData appData) {
      return context.getPackageName ().equals (appData.packageName);
    }
    
    public interface SystemListener {
      
      void onWarning (String message);
      void onSuccess (String message);
      void onError (String message);
      
    }
    
    public boolean isEncrypted (String packageName) throws PackageManager.NameNotFoundException {
      
      PackageData pInfo = getPackageData (packageName);
      return isEncrypted (pInfo);
      
    }
    
    public static boolean isEncrypted (PackageData appData) {
      return appData.appInfo.sourceDir.startsWith ("/mnt/asec/");
    }
    
    public boolean isOnSDCard (String packageName) throws PackageManager.NameNotFoundException {
      
      PackageData pInfo = getPackageData (packageName);
      return isOnSDCard (pInfo);
      
    }
    
    public static boolean isOnSDCard (PackageData appData) {
      return appData.appInfo.sourceDir.startsWith ("/sdext2/");
    }
    
    public boolean isFramework (String packageName) throws PackageManager.NameNotFoundException {
      
      PackageData packageData = getPackageData (packageName);
      return isFramework (packageData);
      
    }
    
    public static boolean isFramework (PackageData appData) {
      return appData.appInfo.sourceDir.startsWith ("/system/framework/");
    }
    
    public void makeSystem (String packageName, SystemListener listener) throws SystemException, SystemRemoveUpdatesException, SystemUndefinedStatusException {
      makeSystem (packageName, listener, true);
    }
    
    public void makeSystem (String packageName, SystemListener listener, boolean remount) throws SystemException, SystemRemoveUpdatesException, SystemUndefinedStatusException {
      
      try {
        
        if (Device.isRoot ()) {
          
          upl.util.List<String> cmds = new upl.util.ArrayList<> ();
          if (remount) cmds = System.remount ("/system", "rw", cmds);
          
          String newFile;
          
          PackageData appData = getPackageData (packageName, GET_LABEL);
          
          if (!isSelf (appData)) {
            
            if (!appData.isSystem && appData.appInfo.sourceDir.endsWith ("/pkg.apk")) {
              
              if (isEncrypted (appData) && listener != null)
                listener.onWarning (appData.title + " is encrypted app and therefore might not be converted to a system app. Continue at your own risk!");
              else if (!isOnSDCard (appData))
                throw new SystemException (appData.title + " is currently installed on SD card. Please move it to internal memory before making it system.");
              
            }
            
            if (new File (appData.appInfo.sourceDir).exists ()) {
              
              cmds = System.killProcess (appData, cmds);
              
              String loliName, newPath, oldPath;
              
              if (appData.isSystem) { // В пользовательские
                
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                  
                  newFile = userPath () + "/" + appData.packageName + "-1.apk"; // /data/app/myapp-1.apk
                  
                  cmds.add ("cp -r -p " + appData.appInfo.sourceDir + " " + newFile);
                  cmds.add ("chmod 644 " + newFile);
                  
                  cmds.add ("rm -rf " + appData.appInfo.sourceDir);
                  
                } else {
                  
                  loliName = new File (appData.appInfo.sourceDir).getName ();
                  
                  oldPath = systemPath () + "/" + loliName; // /system/priv-app/MyApp
                  newPath = userPath () + "/" + appData.packageName + "-1"; // /data/app/myapp-1
                  
                  newFile = newPath + "/base.apk"; // /data/app/myapp-1/MyApp.apk
                  
                  cmds.add ("cp -r -p " + oldPath + " " + newPath);
                  cmds.add ("rm -rf " + oldPath);
                  
                  cmds.add ("mv " + newPath + "/" + loliName + ".apk" + " " + newFile);
                  
                  cmds.add ("chmod -R 755 " + newPath);
                  cmds.add ("chmod 644 " + newFile);
                  
                  //cmds.add ("chown system " + newFile); // TODO
                  //cmds.add ("chgrp system " + newFile);
                  
                }
                
              } else { // В системные
                
                loliName = appData.label.replace (" ", "");
                
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                  
                  newFile = systemPath () + "/" + loliName + ".apk"; // /system/priv-app/MyApp.apk
                  
                  cmds.add ("mkdir " + systemPath ());
                  cmds.add ("touch " + newFile);
                  cmds.add ("cp " + appData.appInfo.sourceDir + " " + newFile);
                  cmds.add ("chmod 664 " + newFile);
                  
                  cmds.add ("rm -rf " + appData.appInfo.sourceDir);
                  
                  cmds.add ("chown system " + newFile);
                  cmds.add ("chgrp system " + newFile);
                  
                } else {
                  
                  oldPath = new File (appData.appInfo.sourceDir).getPath ();
                  newPath = systemPath () + "/" + loliName;
                  
                  newFile = newPath + "/" + loliName + ".apk"; // /system/priv-app/MyApp/MyApp.apk
                  
                  cmds.add ("mkdir " + newPath);
                  cmds.add ("touch " + newFile);
                  cmds.add ("cp -r -p " + oldPath + " " + newPath);
                  
                  cmds.add ("mv " + appData.appInfo.sourceDir + " " + newFile);
                  
                  cmds.add ("chmod -R 755 " + newPath);
                  cmds.add ("chmod 664 " + newFile);
                  
                  cmds.add ("rm -rf " + oldPath);
                  
                }
                
              }
              
              if (remount) cmds = System.remount ("/system", "ro", cmds);
              
              final upl.util.List<String> errors = new upl.util.ArrayList<> ();
              
              Console exec = new Console (new Console.Listener () {
                
                @Override
                public void onExecute (String line, int i) {}
                
                @Override
                public void onSuccess (String line, int i) {}
                
                @Override
                public void onError (String line, int i) {
                  errors.add (line);
                }
                
              });
              
              //exec.sleep = 500;
              exec.shell (Console.su);
              exec.prefix = Device.box ();
              
              exec.query (cmds);
              
              if (Int.size (errors) > 0) throw new SystemException (errors.implode ());
              
              if (new File (newFile).exists ())
                listener.onSuccess (appData.title + " successfully converted to " + (appData.isSystem ? "user" : "system") + " app");
              else
                listener.onError ("Failed to convert " + appData.title + " to " + (appData.isSystem ? "user" : "system") + " app");
              
            } else if (appData.isSystem && appData.appInfo.sourceDir.startsWith ("/data/app/"))
              throw new SystemRemoveUpdatesException ("Can't move " + appData.title + ": Remove installed updates first");
            else
              throw new SystemUndefinedStatusException ("Can't move " + appData.title + ". You might need to reboot your device.");
            
          } else throw new SystemException ("Cannot make " + (appData.isSystem ? "user" : "system") + " self app");
          
        } else throw new Device.NoRootException ("Device must be rooted");
        
      } catch (PackageManager.NameNotFoundException | Device.NoRootException | System.SystemException | ConsoleException e) {
        throw new SystemException (e);
      }
      
    }
    
    public void start (String packageName) throws PackageManager.NameNotFoundException {
      start (packageName, true);
    }
    
    public void start (String packageName, boolean newTask) throws PackageManager.NameNotFoundException {
      
      Intent intent = pm.getLaunchIntentForPackage (packageName);
      
      if (intent != null) {
        
        if (newTask) intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity (intent);
        
      } else throw new PackageManager.NameNotFoundException (packageName);
      
    }
    
    public static class SystemException extends Exception {
      
      SystemException (String msg) {
        super (msg);
      }
      
      SystemException (Exception e) {
        super (e);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public static class SystemRemoveUpdatesException extends Exception {
      
      private SystemRemoveUpdatesException (String msg) {
        super (msg);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public static class SystemUndefinedStatusException extends Exception {
      
      private SystemUndefinedStatusException (String msg) {
        super (msg);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public static void restart (Activity activity, Class<?> newActivity) {
      
      Intent intent = new Intent (activity, newActivity);
      restart (activity, intent);
      
    }
    
    public static void restart (Activity activity, Intent intent) {
      
      intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
      
      if (intent.getComponent () != null) {
        
        intent.setAction (Intent.ACTION_MAIN);
        intent.addCategory (Intent.CATEGORY_LAUNCHER);
        
      }
      
      activity.finish ();
      activity.startActivity (intent);
      
      System.killProcess ();
      
    }
    
    public void showIcon (Class<?> activity) {
      componentState (activity, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }
    
    public void hideIcon (Class<?> activity) {
      componentState (activity, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }
    
    public void componentState (Class<?> activity, int state) {
      
      ComponentName componentName = new ComponentName (context, activity);
      pm.setComponentEnabledSetting (componentName, state, PackageManager.DONT_KILL_APP);
      
    }
    
    public static String googlePlayLink (Context context) {
      return googlePlayLink (context.getPackageName ());
    }
    
    public static String googlePlayLink (String id) {
      return "market://details?id=" + id;
    }
    
    public static URL googlePlayRawLink (String id) throws MalformedURLException {
      return googlePlayRawLink (id, "");
    }
    
    public static URL googlePlayRawLink (String id, String lang) throws MalformedURLException {
      
      String link = "https://play.google.com/store/apps/details?id=" + id;
      if (!lang.equals ("")) link += "&hl=" + lang;
      
      return new URL (link);
      
    }
    
    public void openPlayUrl () {
      OS.openUrl (context, googlePlayLink (context));
    }
    
    public static void openPlayUrl (Context context) {
      OS.openUrl (context, googlePlayLink (context));
    }
    
    public void openPlayUrl (String id) {
      OS.openUrl (context, googlePlayLink (id));
    }
    
    public List<String> extractApp (String target, File dest) throws ConsoleException, OutOfMemoryException {
      
      final List<String> errors = new ArrayList<> ();
      
      try {
        new File (target).copy (dest);
      } catch (IOException e) {
        
        if (Device.isRoot ()) {
          
          Console exec = new Console (new Console.Listener () {
            
            @Override
            public void onExecute (String line, int i) {}
            
            @Override
            public void onSuccess (String line, int i) {}
            
            @Override
            public void onError (String line, int i) {
              errors.add (System.error (context, line));
            }
            
          });
          
          exec.shell (Console.su);
          exec.prefix = Device.box ();
          
          exec.query (extractApp (target, dest, new upl.util.ArrayList<String> ()));
          
        }
        
      }
      
      return errors;
      
    }
    
    public upl.util.List<String> extractApp (String target, File dest, upl.util.List<String> cmds) {
      
      cmds.add ("cp -f " + target + " \"" + dest + "\"");
      return cmds;
      
    }
    
  }