  package ru.ointeractive.andromeda.apps;
  /*
    Created by Acuna on 17.07.2017
  */
  
  import android.Manifest;
  import android.app.PendingIntent;
  import android.app.Service;
  import android.content.BroadcastReceiver;
  import android.content.Context;
  import android.content.Intent;
  import android.content.IntentFilter;
  import android.content.pm.IPackageDeleteObserver;
  import android.content.pm.IPackageInstallObserver;
  import android.content.pm.PackageInstaller;
  import android.content.pm.PackageManager;
  import android.net.Uri;
  import android.os.Build;
  import android.os.IBinder;
  import android.util.SparseArray;
  
  import java.io.IOException;
  import java.io.InputStream;
  import java.io.OutputStream;
  import java.lang.reflect.Method;
  import java.util.regex.Matcher;
  import java.util.regex.Pattern;
	
  import ru.ointeractive.andromeda.Device;
  import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.andromeda.System;
  import upl.core.Console;
  import upl.core.File;
  import upl.core.HttpRequest;
  import upl.core.Int;
  import upl.core.Net;
  import upl.core.exceptions.ConsoleException;
  import upl.util.ArrayList;
  import upl.util.List;
	
  public class AppsService extends Service {
    
    public final static int STATUS_START = 100;
    public final static int STATUS_FINISH = 200;
    public final static int STATUS_ACTIVITY_FINISH = 201;
    public final static int STATUS_PROCESS = 300;
    public final static int STATUS_ERROR = 400;
    
    public final static String ACTION_INSTALL = "INSTALL";
    public final static String ACTION_UNINSTALL = "UNINSTALL";
    public final static String ACTION_DOWNLOAD = "DOWNLOAD";
    public final static String ACTION_INSTALL_SUCCEEDED = "INSTALL_SUCCEEDED";
    public final static String ACTION_UNINSTALL_SUCCEEDED = "UNINSTALL_SUCCEEDED";
    
    private static String installKeys = "-r -d";
    
    private Context context;
    
    public AppsService () {}
    
    public AppsService (Context context) {
      this.context = context;
    }
    
    @Override
    public int onStartCommand (final Intent intent, int flags, final int id) {
      
      final Intent bIntent = OS.receiverIntent (context);
      
      bIntent.putExtras (intent);
      
      if (intent.getIntExtra ("id", 0) == 0)
        bIntent.putExtra ("id", id);
      
      bIntent.putExtra ("status", 0);
      bIntent.putExtra ("result", "");
      
      switch (intent.getStringExtra ("action")) {
        
        case ACTION_DOWNLOAD: {
          
          if (!new File (intent.getStringExtra ("local_file")).exists () || intent.getBooleanExtra ("oblige", false)) {
            
            new ru.ointeractive.andromeda.network.Net.Download (new Net.ProgressListener () {
              
              @Override
              public void onStart (long size) {
                
                bIntent.putExtra ("status", STATUS_START);
                
                if (size == -1) size = intent.getLongExtra ("size", 0);
                bIntent.putExtra ("size", size);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onProgress (long length, long size) {
                
                bIntent.putExtra ("status", STATUS_PROCESS);
                bIntent.putExtra ("length", length);
                
                if (size == -1) size = intent.getLongExtra ("size", 0);
                bIntent.putExtra ("size", size);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onError (int code, String result) {
                
                bIntent.putExtra ("status", STATUS_START);
                
                sendBroadcast (bIntent);
                
                bIntent.putExtra ("status", STATUS_ERROR);
                bIntent.putExtra ("code", code);
                bIntent.putExtra ("result", result);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onFinish (int code, String result) {
                
                bIntent.putExtra ("status", STATUS_FINISH);
                bIntent.putExtra ("code", code);
                bIntent.putExtra ("result", result);
                
                sendBroadcast (bIntent);
                
                try {
                  install (intent, bIntent);
                } catch (AppsServiceException e) {
                  
                  bIntent.putExtra ("status", STATUS_ERROR);
                  bIntent.putExtra ("error", e);
                  
                  sendBroadcast (bIntent);
                  
                }
                
              }
              
            }, intent.getLongExtra ("size", -1), HttpRequest.defTimeout).execute (intent.getStringExtra ("remote_file"), intent.getStringExtra ("local_file"), intent.getStringExtra ("user_agent"));
            
          } else {
            
            bIntent.putExtra ("status", STATUS_START);
            sendBroadcast (bIntent);
            
            if (intent.getBooleanExtra ("is_root", Device.isRoot ()))
              bIntent.putExtra ("status", STATUS_FINISH);
            else
              bIntent.putExtra ("status", STATUS_ACTIVITY_FINISH);
            
            bIntent.putExtra ("code", Net.HTTP_CODE_OK);
            bIntent.putExtra ("result", upl.core.Net.httpCodes ().get (Net.HTTP_CODE_OK));
            
            sendBroadcast (bIntent);
            
            try {
              install (intent, bIntent);
            } catch (AppsServiceException e) {
              
              bIntent.putExtra ("status", STATUS_ERROR);
              bIntent.putExtra ("error", e);
              
              sendBroadcast (bIntent);
              
            }
            
          }
          
          break;
          
        }
        
        case ACTION_INSTALL: {
          
          bIntent.putExtra ("status", STATUS_START);
          
          sendBroadcast (bIntent);
          
          try {
            
            install (intent.getStringExtra ("name"), intent.getStringExtra ("file"), intent.getIntExtra ("flags", 2), intent.getBooleanExtra ("is_root", Device.isRoot ()), new Listener () {
              
              @Override
              public void onFinish (String result) {
                
                bIntent.putExtra ("status", STATUS_FINISH);
                bIntent.putExtra ("result", result);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onActivityFinish () {
                
                bIntent.putExtra ("status", STATUS_ACTIVITY_FINISH);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onProgress (long length, long size) {
                
                bIntent.putExtra ("status", STATUS_PROCESS);
                bIntent.putExtra ("length", length);
                bIntent.putExtra ("size", size);
                
                sendBroadcast (bIntent);
                
              }
              
            });
            
          } catch (AppsServiceException e) {
            
            bIntent.putExtra ("status", STATUS_ERROR);
            bIntent.putExtra ("error", e);
            
            sendBroadcast (bIntent);
            
          }
          
          break;
          
        }
        
        case ACTION_UNINSTALL: {
          
          try {
            
            bIntent.putExtra ("status", STATUS_START);
            
            sendBroadcast (bIntent);
            
            uninstall (intent.getStringExtra ("name"), intent.getBooleanExtra ("is_root", Device.isRoot ()), new Listener () {
              
              @Override
              public void onFinish (String result) {
                
                bIntent.putExtra ("status", STATUS_FINISH);
                bIntent.putExtra ("result", result);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onActivityFinish () {
                
                bIntent.putExtra ("status", STATUS_ACTIVITY_FINISH);
                
                sendBroadcast (bIntent);
                
              }
              
              @Override
              public void onProgress (long length, long size) {
                
                bIntent.putExtra ("status", STATUS_PROCESS);
                bIntent.putExtra ("length", length);
                bIntent.putExtra ("size", size);
                
                sendBroadcast (bIntent);
                
              }
              
            });
            
          } catch (Exception e) {
            
            bIntent.putExtra ("status", STATUS_ERROR);
            bIntent.putExtra ("error", e);
            
            sendBroadcast (bIntent);
            
          }
          
          break;
          
        }
        
      }
      
      stopSelf (id);
      
      return super.onStartCommand (intent, flags, id);
      
    }
    
    private void install (final Intent intent, final Intent bIntent) throws AppsServiceException {
      
      install (intent.getStringExtra ("name"), intent.getStringExtra ("local_file"), intent.getIntExtra ("flags", 2), intent.getBooleanExtra ("is_root", Device.isRoot ()), new Listener () {
        
        @Override
        public void onFinish (String result) {
          
          bIntent.putExtra ("action", ACTION_INSTALL);
          bIntent.putExtra ("status", STATUS_FINISH);
          bIntent.putExtra ("result", result);
          
          sendBroadcast (bIntent);
          
        }
        
        @Override
        public void onActivityFinish () {
          
          bIntent.putExtra ("action", ACTION_INSTALL);
          bIntent.putExtra ("status", STATUS_ACTIVITY_FINISH);
          
          sendBroadcast (bIntent);
          
        }
        
        @Override
        public void onProgress (long length, long size) {
          
          bIntent.putExtra ("action", ACTION_INSTALL);
          bIntent.putExtra ("status", STATUS_PROCESS);
          
          bIntent.putExtra ("length", length);
          bIntent.putExtra ("size", size);
          
          sendBroadcast (bIntent);
          
        }
        
      });
      
    }
    
    @Override
    public IBinder onBind (Intent intent) {
      return null;
    }
    
    public static class AppsServiceException extends Exception {
      
      private AppsServiceException (String msg) {
        super (msg);
      }
      
      private AppsServiceException (List<String> errors) {
        super (errors.implode ());
      }
      
      private AppsServiceException (Exception e) {
        super (e);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    private static class InstallReceiver extends BroadcastReceiver {
      
      private Listener listener;
      private String mess;
      
      private InstallReceiver (Listener listener, String mess) {
        
        this.listener = listener;
        this.mess = mess;
        
      }
      
      @Override
      public void onReceive (Context context, Intent intent) {
        listener.onFinish (mess);
      }
      
    }
    
    public static SparseArray<String> getInstallMessages () {
      
      SparseArray<String> mess = new SparseArray<> ();
      
      mess.put (1, ACTION_INSTALL_SUCCEEDED);
      
      mess.put (-1, "INSTALL_FAILED_ALREADY_EXISTS");
      mess.put (-2, "INSTALL_FAILED_INVALID_APK");
      mess.put (-3, "INSTALL_FAILED_INVALID_URI");
      mess.put (-4, "INSTALL_FAILED_INSUFFICIENT_STORAGE");
      mess.put (-5, "INSTALL_FAILED_DUPLICATE_PACKAGE");
      mess.put (-6, "INSTALL_FAILED_NO_SHARED_USER");
      mess.put (-7, "INSTALL_FAILED_UPDATE_INCOMPATIBLE");
      mess.put (-8, "INSTALL_FAILED_SHARED_USER_INCOMPATIBLE");
      mess.put (-9, "INSTALL_FAILED_MISSING_SHARED_LIBRARY");
      mess.put (-10, "INSTALL_FAILED_REPLACE_COULDNT_DELETE");
      mess.put (-11, "INSTALL_FAILED_DEXOPT");
      mess.put (-12, "INSTALL_FAILED_OLDER_SDK");
      mess.put (-13, "INSTALL_FAILED_CONFLICTING_PROVIDER");
      mess.put (-14, "INSTALL_FAILED_NEWER_SDK");
      mess.put (-15, "INSTALL_FAILED_TEST_ONLY");
      mess.put (-16, "INSTALL_FAILED_CPU_ABI_INCOMPATIBLE");
      mess.put (-17, "INSTALL_FAILED_MISSING_FEATURE");
      mess.put (-18, "INSTALL_FAILED_CONTAINER_ERROR");
      mess.put (-19, "INSTALL_FAILED_INVALID_INSTALL_LOCATION");
      mess.put (-20, "INSTALL_FAILED_MEDIA_UNAVAILABLE");
      mess.put (-21, "INSTALL_FAILED_VERIFICATION_TIMEOUT");
      mess.put (-22, "INSTALL_FAILED_VERIFICATION_TIMEOUT");
      mess.put (-23, "INSTALL_FAILED_PACKAGE_CHANGED");
      mess.put (-24, "INSTALL_FAILED_UID_CHANGED");
      mess.put (-25, "INSTALL_FAILED_VERSION_DOWNGRADE");
      mess.put (-100, "INSTALL_PARSE_FAILED_NOT_APK");
      mess.put (-101, "INSTALL_PARSE_FAILED_BAD_MANIFEST");
      mess.put (-102, "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION");
      mess.put (-103, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
      mess.put (-104, "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES");
      mess.put (-105, "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
      mess.put (-106, "INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME");
      mess.put (-107, "INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID");
      mess.put (-108, "INSTALL_PARSE_FAILED_MANIFEST_MALFORMED");
      mess.put (-109, "INSTALL_PARSE_FAILED_MANIFEST_EMPTY");
      mess.put (-110, "INSTALL_FAILED_INTERNAL_ERROR");
      mess.put (-111, "INSTALL_FAILED_USER_RESTRICTED");
      mess.put (-112, "INSTALL_FAILED_DUPLICATE_PERMISSION");
      mess.put (-112, "INSTALL_FAILED_NO_MATCHING_ABIS");
      
      return mess;
      
    }
    
    public void install (String name, String fileUrl, int flags, boolean isRoot, Listener listener) throws AppsServiceException {
      install (name, new File (fileUrl), flags, isRoot, listener);
    }
    
    public void install (String name, File fileUrl, int flags, boolean isRoot, Listener listener) throws AppsServiceException {
      
      try {
        
        if (fileUrl.exists ())
          install (name, Uri.fromFile (fileUrl), flags, isRoot, listener);
        else
          throw new IOException ("File not exists: " + fileUrl.getAbsolutePath ());
        
      } catch (IOException e) {
        throw new AppsServiceException (e);
      }
      
    }
    
    public void install (final String name, Uri file, int flags, boolean isRoot, final Listener listener) throws AppsServiceException { // Установщик, использующий reflection. Подсмотрен у F-Droid. Путь оказался ошибочным.
      
      try {
        
        final AppsManager apps = new AppsManager (context);
        final SparseArray<String> mess = getInstallMessages ();
        
        if (isRoot && apps.hasPermission (Manifest.permission.INSTALL_PACKAGES)) {
          
          /*final IntentFilter filter = new IntentFilter ();
          
          filter.addAction (Intent.ACTION_PACKAGE_INSTALL);
          filter.addAction (Intent.ACTION_PACKAGE_ADDED);
          filter.addAction (Intent.ACTION_PACKAGE_REPLACED);
          
          filter.addDataScheme ("package");*/
          
          if (Build.VERSION.SDK_INT < 21) {
            
            IPackageInstallObserver.Stub observer = new IPackageInstallObserver.Stub () {
              
              @Override
              public void packageInstalled (String packageName, int returnCode) {
                
                String message = mess.get (returnCode);
                //context.registerReceiver (new InstallReceiver (listener, message), filter);
                listener.onFinish (message);
                
              }
              
            };
            
            Class<?>[] types = new Class[] {Uri.class, IPackageInstallObserver.class, int.class, String.class};
            Method method = apps.pm.getClass ().getMethod ("installPackage", types);
            
            method.invoke (apps.pm, file, observer, flags, null);
            
          } else {
            
            PackageInstaller.Session session = null;
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams (PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            
            PackageInstaller packageInstaller = apps.pm.getPackageInstaller ();
            
            try {
              
              int sessionId = packageInstaller.createSession (params);
              
              int bLength = 65536;
              byte[] buffer = new byte[bLength];
              
              session = packageInstaller.openSession (sessionId);
              
              InputStream in = context.getContentResolver ().openInputStream (file);
              OutputStream out = session.openWrite ("PackageInstaller", 0, -1);
              
              long size = Int.size (new File (file.getPath ()));
              long chunksNum = (size / bLength);
              
              try {
                
                long c, i = 0;
                
                if (in != null)
                  while ((c = in.read (buffer)) != -1) {
                    ++i;
                    
                    out.write (buffer, 0, (int) c);
                    listener.onProgress (i, chunksNum);
                    
                  }
                
                session.fsync (out);
                
              } finally {
                
                if (in != null) in.close ();
                out.close ();
                
              }
              
              Intent intent = new Intent (OS.broadcastAction (context, "ACTION_INSTALL_COMMIT", 1));
              PendingIntent pendingIntent = PendingIntent.getBroadcast (context, sessionId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
              
              session.commit (pendingIntent.getIntentSender ());
              
              //context.registerReceiver (new InstallReceiver (listener, mess.get (1)), filter);
              listener.onFinish (mess.get (1));
              
            } catch (IOException e) {
              throw new AppsServiceException (e);
            } finally {
              if (session != null) session.close ();
            }
            
          }
          
        } else {
          
          apps.install (file);
          listener.onActivityFinish ();
          
        }
        
      } catch (Exception e) {
        throw new AppsServiceException (e);
      }
      
    }
    
    public static SparseArray<String> getUninstallMessages () {
      
      SparseArray<String> mess = new SparseArray<> ();
      
      mess.put (1, ACTION_UNINSTALL_SUCCEEDED);
      mess.put (-1, "DELETE_FAILED_APK_NOT_FOUND");
      mess.put (-2, "DELETE_FAILED_DEVICE_POLICY_MANAGER");
      mess.put (-3, "DELETE_FAILED_USER_RESTRICTED");
      mess.put (-4, "DELETE_FAILED_OWNER_BLOCKED");
      mess.put (-5, "DELETE_FAILED_ABORTED");
      
      return mess;
      
    }
    
    public void uninstall (String id, boolean isRoot, final Listener listener) throws Exception {
      
      final SparseArray<String> mess = getUninstallMessages ();
      
      final AppsManager apps = new AppsManager (context);
      PackageData appData = apps.getPackageData (id);
      
      if (!apps.isSelf (appData)) {
        
        boolean isSystem = (isRoot && appData.isSystem);
        
        if (!apps.isDeviceOwner (id)) {
          
          if (isSystem || AppsManager.isFramework (appData)) {
            
            Console exec = new Console (new Console.Listener () {
              
              @Override
              public void onExecute (String line, int i) {}
              
              @Override
              public void onSuccess (String line, int i) {}
              
              @Override
              public void onError (String line, int i) {
                listener.onFinish (line);
              }
              
            });
            
            exec.shell (Console.su);
            
            List<String> cmds = new ArrayList<> ();
            
            exec.query (cmds); // TODO
            
            if (!new File (appData.appInfo.sourceDir).exists ())
              listener.onFinish (mess.get (1));
            
            //context.registerReceiver (new InstallReceiver (listener, mess.get (1)), filter);
            
          } else if (apps.hasPermission (Manifest.permission.DELETE_PACKAGES)) {
          
          /*final IntentFilter filter = new IntentFilter ();
          
          filter.addAction (Intent.ACTION_PACKAGE_REMOVED);
          filter.addAction (Intent.ACTION_PACKAGE_FULLY_REMOVED);
          
          filter.addDataScheme ("package");*/
            
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
              
              IPackageDeleteObserver.Stub observer = new IPackageDeleteObserver.Stub () {
                
                @Override
                public void packageDeleted (String packageName, int returnCode) {
                  
                  String message = mess.get (returnCode);
                  //context.registerReceiver (new InstallReceiver (listener, message), filter);
                  listener.onFinish (message);
                  
                }
                
              };
              
              Class<?>[] types = new Class[] {String.class, IPackageDeleteObserver.class, int.class};
              Method method = apps.pm.getClass ().getMethod ("deletePackage", types);
              
              method.invoke (apps.pm, id, observer, 0);
              
            } else {
              
              //String[] appData = AppsManager.getPackageData (this);
              //apps.pm.setInstallerPackageName (id, appData[0]);
              
              Intent intent = new Intent (OS.broadcastAction (context, "ACTION_UNINSTALL_COMMIT", 1));
              
	            PendingIntent pendingIntent = PendingIntent.getBroadcast (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
              
              PackageInstaller packageInstaller = apps.pm.getPackageInstaller ();
              packageInstaller.uninstall (id, pendingIntent.getIntentSender ());
              
              //context.registerReceiver (new InstallReceiver (listener, mess.get (1)), filter);
              listener.onFinish (mess.get (1));
              
            }
            
          } else {
            
            apps.uninstall (id);
            listener.onActivityFinish ();
            
          }
          
        } else listener.onFinish (mess.get (-4));
        
      } else listener.onFinish (mess.get (-5));
      
    }
    
    private int i;
    
    public List<String> install (File file, boolean isRoot) throws AppsServiceException {
      return install (file, isRoot, null);
    }
    
    public List<String> install (File file, boolean isRoot, BroadcastReceiver receiver) throws AppsServiceException {
      return install (file, isRoot, receiver, installKeys);
    }
    
    public List<String> install (File file, boolean isRoot, BroadcastReceiver receiver, String keys) throws AppsServiceException {
      
      final List<String> errors = new ArrayList<> (), success = new ArrayList<> ();
      
      try {
        
        if (isRoot) {
          
          Console exec = new Console ();
          
          exec.shell (Console.su);
          
          exec.addListener (new Console.Listener () {
            
            @Override
            public void onExecute (String line, int i) {}
            
            @Override
            public void onSuccess (String line, int i) {
              success.add (line);
            }
            
            @Override
            public void onError (String line, int i) {
              
              String[] data = failureData (line);
              
              if (data[1] != null)
                errors.add (data[1]);
              
            }
            
          });
          
          List<String> cmds = install (file, new ArrayList<String> (), keys);
          
          exec.query (cmds);
          
        } else {
          
          if (receiver != null) installFilter (receiver);
          
          AppsManager apps = new AppsManager (context);
          apps.install (file);
          
        }
        
        if (Int.size (errors) > 0) {
          
          ++i;
          
          if (errors.get (0).equals ("Segmentation fault") && i < 5)
            install (file, isRoot, receiver);
          else
            throw new AppsServiceException (errors);
          
        } else i = 0;
        
      } catch (ConsoleException e) {
        throw new AppsServiceException (e);
      }
      
      return success;
      
    }
    
    public static String[] failureData (String line) {
      
      String[] data = new String[2];
      
      Matcher match = Pattern.compile ("pkg:\\s+(.+)").matcher (line);
      
      if (!match.find ()) {
        
        data[0] = null;
        
        match = Pattern.compile ("Failure\\s+[(.+)]").matcher (line);
        
        if (match.find ())
          data[1] = match.group (1);
        else
          data[1] = line;
        
      } else {
        
        data[0] = match.group (1);
        data[1] = null;
        
      }
      
      return data;
      
    }
    
    public List<String> install (File file, List<String> cmds) {
      return install (file, cmds, installKeys);
    }
    
    public List<String> install (File file, List<String> cmds, String keys) {
      
      cmds.add ("pm install " + keys + " " + file);
      
      return cmds;
      
    }
    
    public List<String> uninstall (PackageData packageData, boolean isRoot) throws AppsServiceException {
      return uninstall (packageData, isRoot, null);
    }
    
    public List<String> uninstall (PackageData packageData, boolean isRoot, BroadcastReceiver receiver) throws AppsServiceException {
      
      final List<String> errors = new ArrayList<> (), success = new ArrayList<> ();
      List<String> cmds = new ArrayList<> ();
      
      try {
        
        if (isRoot) {
          
          Console console = new Console ();
          
          console.shell (Console.su);
          
          console.addListener (new Console.Listener () {
            
            @Override
            public void onExecute (String line, int i) {}
            
            @Override
            public void onSuccess (String line, int i) {
              success.add (line);
            }
            
            @Override
            public void onError (String line, int i) {
              
              Matcher match = Pattern.compile ("pkg:\\s+(.+)").matcher (line);
              
              if (!match.find ()) {
                
                match = Pattern.compile ("Failure\\s+[(.+)]").matcher (line);
                if (match.find ()) line = match.group (1);
                
                errors.add (line);
                
              }
              
            }
            
          });
          
          if (packageData.isSystem) {
            
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || AppsManager.isFramework (packageData))
              cmds.add ("rm -f " + packageData.appInfo.sourceDir);
            else
              cmds.add ("rm -rf " + new File (packageData.appInfo.sourceDir).getPath ());
            
            cmds.add ("rm -rf /data/data/" + packageData.packageName);
            cmds.add ("rm -rf " + OS.getExternalFilesDir (context, packageData.packageName));
            
          } else cmds = uninstall (packageData.packageName, cmds);
          
          console.query (cmds);
          
          if (Int.size (errors) > 0) {
            
            ++i;
            
            if (errors.get (0).equals ("Segmentation fault") && i < 5)
              uninstall (packageData, true, receiver);
            else
              throw new AppsServiceException (errors);
            
          } else i = 0;
          
        } else {
          
          if (receiver != null) uninstallFilter (receiver);
          
          AppsManager apps = new AppsManager (context);
          apps.uninstall (packageData.packageName);
          
        }
        
      } catch (ConsoleException | PackageManager.NameNotFoundException e) {
        throw new AppsServiceException (e);
      }
      
      return success;
      
    }
    
    public static boolean systemRemount (boolean isRoot) throws System.SystemException {
      return systemRemount (isRoot, true);
    }
    
    public static boolean finishSystemRemount (boolean isRoot) throws System.SystemException {
      return finishSystemRemount (isRoot, true);
    }
    
    public static boolean systemRemount (String type, boolean isRoot, boolean isSystem) throws System.SystemException {
      return !(isRoot && isSystem) || System.remount ("/system", type);
    }
    
    public static boolean systemRemount (boolean isRoot, boolean isSystem) throws System.SystemException {
      return systemRemount ("rw", isRoot, isSystem);
    }
    
    public static boolean finishSystemRemount (boolean isRoot, boolean isSystem) throws System.SystemException {
      return systemRemount ("ro", isRoot, isSystem);
    }
    
    public List<String> uninstall (String id, List<String> cmds) {
      
      cmds.add ("pm uninstall " + (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ? "--user 0 " : "") + id);
      return cmds;
      
    }
    
    public interface Listener {
      
      void onProgress (long length, long size);
      void onFinish (String result);
      void onActivityFinish ();
      
    }
    
    private BroadcastReceiver receiver;
    
    private void installFilter (BroadcastReceiver receiver) {
      
      this.receiver = receiver;
      
      IntentFilter filter = new IntentFilter ();
      
      filter.addAction (Intent.ACTION_PACKAGE_INSTALL);
      filter.addAction (Intent.ACTION_PACKAGE_ADDED);
      filter.addAction (Intent.ACTION_PACKAGE_REPLACED);
      
      filter.addDataScheme ("package");
      
      context.registerReceiver (receiver, filter);
      
    }
    
    private void uninstallFilter (BroadcastReceiver receiver) {
      
      this.receiver = receiver;
      
      IntentFilter filter = new IntentFilter ();
      
      filter.addAction (Intent.ACTION_PACKAGE_REMOVED);
      filter.addAction (Intent.ACTION_PACKAGE_FULLY_REMOVED);
      
      filter.addDataScheme ("package");
      
      context.registerReceiver (receiver, filter);
      
    }
    
    @Override
    public void onDestroy () {
      
      if (receiver != null)
        context.unregisterReceiver (receiver);
      
    }
    
  }