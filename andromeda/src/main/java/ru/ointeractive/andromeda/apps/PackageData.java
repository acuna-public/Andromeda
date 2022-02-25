  package ru.ointeractive.andromeda.apps;
  /*
   Created by Acuna on 13.11.2018
  */
  
  import android.app.usage.StorageStats;
  import android.app.usage.StorageStatsManager;
  import android.content.ComponentName;
  import android.content.Context;
  import android.content.Intent;
  import android.content.pm.ActivityInfo;
  import android.content.pm.ApplicationInfo;
  import android.content.pm.IPackageStatsObserver;
  import android.content.pm.PackageInfo;
  import android.content.pm.PackageManager;
  import android.content.res.Configuration;
  import android.content.res.Resources;
  import android.graphics.drawable.Drawable;
  import android.os.Build;
  import android.os.RemoteException;
  import android.os.UserHandle;
  
  import java.io.IOException;
  import java.lang.reflect.Method;
  import java.util.Locale;

  import ru.ointeractive.andromeda.OS;
  
  public class PackageData {
    
    private AppsManager appsManager;
    public PackageInfo packageInfo;
    public String packageName, title, version, label;
    public ApplicationInfo appInfo;
    public int versionCode;
    public Drawable icon;
    public boolean isSystem;
    
    PackageData (AppsManager appsManager, PackageInfo packageInfo, int type) {
      
      this.appsManager = appsManager;
      
      this.packageInfo = packageInfo;
      
      packageName = packageInfo.packageName;
      appInfo = packageInfo.applicationInfo;
      title = appInfo.loadLabel (appsManager.pm).toString ();
      version = packageInfo.versionName.replace (" ", "-").toLowerCase (); // 1.0
      versionCode = packageInfo.versionCode; // 1
      isSystem = appsManager.isSystem (packageInfo);
      icon = appInfo.loadIcon (appsManager.pm);
      
      if (type == AppsManager.GET_LABEL) {
        
        label = getLabel (packageName);
        if (label == null) label = title;
        
      }
      
    }
    
    private String getLabel (String appName) {
      
      String label = null;
      
      try {
        
        Intent launchIntent = appsManager.pm.getLaunchIntentForPackage (appName);
        
        if (launchIntent != null) {
          
          ComponentName cn = launchIntent.getComponent ();
          ActivityInfo info = appsManager.pm.getActivityInfo (cn, 0);
          
          label = info.loadLabel (appsManager.pm).toString ();
          
        }
        
      } catch (PackageManager.NameNotFoundException e) {
        // empty
      }
      
      return label;
      
    }
    
    public String getLocalizedLabel (String lang) {
      
      try {
        
        Configuration config = new Configuration ();
        
        config.locale = new Locale (lang);
        
        Resources res = appsManager.pm.getResourcesForApplication (packageName);
        res.updateConfiguration (config, appsManager.context.getResources ().getDisplayMetrics ());
        
        return new upl.type.String (res.getString (appInfo.labelRes)).ucfirst ();
        
      } catch (Resources.NotFoundException | PackageManager.NameNotFoundException e) {
        return new upl.type.String (title).ucfirst ();
      }
      
    }
    
    public interface SizeListener {
      void onSizeGet (PackageStats pStats, boolean succeeded);
    }
    
    private static class SizeObserver extends IPackageStatsObserver.Stub {
      
      private SizeListener listener;
      
      private SizeObserver (SizeListener listener) {
        this.listener = listener;
      }
      
      @Override
      public void onGetStatsCompleted (android.content.pm.PackageStats pStats, boolean succeeded) throws RemoteException {
        
        PackageStats pStatsNew = new PackageStats ();
        
        pStatsNew.codeSize = pStats.codeSize;
        pStatsNew.dataSize = pStats.dataSize;
        pStatsNew.cacheSize = pStats.cacheSize;
        
        if (OS.SDK >= 14) { // SUPPORT 14
        	
	        pStatsNew.externalCodeSize = pStats.externalCodeSize;
	        pStatsNew.externalDataSize = pStats.externalDataSize;
	        pStatsNew.externalCacheSize = pStats.externalCacheSize;
	        pStatsNew.externalMediaSize = pStats.externalMediaSize;
	        pStatsNew.externalObbSize = pStats.externalObbSize;
	        
        }
        
        listener.onSizeGet (pStatsNew, succeeded);
        
      }
      
    }
    
    public static class PackageStats {
      
      public long codeSize = 0, dataSize = 0, cacheSize = 0;
      public long externalCodeSize = 0, externalDataSize = 0, externalCacheSize = 0, externalMediaSize = 0, externalObbSize = 0;
      
    }
    
    public void getSize (SizeListener listener) throws AppsManager.SystemException {
      
      if (Build.VERSION.SDK_INT >= 26) {
        
        try {
          
          StorageStatsManager storageStatsManager = (StorageStatsManager) appsManager.context.getSystemService (Context.STORAGE_STATS_SERVICE);
          
          ApplicationInfo ai = appsManager.getAppInfo (packageName);
          StorageStats storageStats = storageStatsManager.queryStatsForUid (ai.storageUuid, appsManager.getAppInfo ().uid);
          
          PackageStats pStatsNew = new PackageStats ();
          
          pStatsNew.codeSize = storageStats.getAppBytes ();
          pStatsNew.dataSize = storageStats.getDataBytes ();
          pStatsNew.cacheSize = storageStats.getCacheBytes ();
          
          listener.onSizeGet (pStatsNew, true);
          
        } catch (PackageManager.NameNotFoundException | IOException e) {
          throw new AppsManager.SystemException (e);
        }
        
      } else {
        
        try {
          
          Class<?> clz = appsManager.pm.getClass ();
          
          if (Build.VERSION.SDK_INT > 16) {
            
            Method myUserId = UserHandle.class.getMethod ("myUserId");
            int userID = (Integer) myUserId.invoke (appsManager.pm);
            
            Method getPackageSizeInfo = clz.getMethod ("getPackageSizeInfo", String.class, int.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke (appsManager.pm, packageName, userID, new SizeObserver (listener));
            
          } else {
            
            Method getPackageSizeInfo = clz.getMethod ("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke (appsManager.pm, packageName, new SizeObserver (listener));
            
          }
          
        } catch (Exception e) {
          throw new AppsManager.SystemException (e);
        }
        
      }
      
    }
    
  }