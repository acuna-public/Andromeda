  package pro.acuna.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
  
  import android.app.Activity;
  import android.app.AlarmManager;
  import android.app.Notification;
  import android.app.NotificationManager;
  import android.app.PendingIntent;
  import android.content.BroadcastReceiver;
  import android.content.ContentValues;
  import android.content.Context;
  import android.content.Intent;
  import android.content.IntentFilter;
  import android.content.SharedPreferences;
  import android.content.pm.PackageManager;
  import android.content.res.AssetManager;
  import android.content.res.Resources;
  import android.database.Cursor;
  import android.net.Uri;
  import android.os.Bundle;
  import android.os.Environment;
  import android.widget.Toast;
  
  import org.json.JSONArray;
  import org.json.JSONException;
  import org.json.JSONObject;
  
  import java.io.DataInputStream;
  import java.io.File;
  import java.io.IOException;
  import java.io.InputStream;
  import java.net.URL;
  import java.nio.charset.Charset;
  import java.util.ArrayList;
  import java.util.LinkedHashMap;
  import java.util.List;
  import java.util.Map;
  
  import pro.acuna.jabadaba.Arrays;
  import pro.acuna.jabadaba.Files;
  import pro.acuna.jabadaba.Int;
  import pro.acuna.jabadaba.Locales;
  import pro.acuna.jabadaba.Strings;
  import pro.acuna.jabadaba.System;
  import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
  
  public class OS {
    
    public static void share (Activity activity, String subject, String text, String title) {
      
      Intent intent = new Intent (Intent.ACTION_SEND);
      
      intent.setType ("text/*");
      
      intent.putExtra (Intent.EXTRA_SUBJECT, subject);
      intent.putExtra (Intent.EXTRA_TEXT, text);
      
      activity.startActivity (Intent.createChooser (intent, title));
      
    }
    
    public static void share (Activity activity, File file, String title) {
      
      Intent intent = new Intent (Intent.ACTION_SEND);
      
      intent.setType (Files.getMimeType (file.getName ()));
      intent.putExtra (Intent.EXTRA_STREAM, Uri.fromFile (file));
      
      activity.startActivity (Intent.createChooser (intent, title));
      
    }
    
    public static String appFilesPath (Context context) {
      return context.getFilesDir ().getAbsolutePath ();
    }
    
    public static File appFilesPath (Context context, String fileName) {
      return new File (appFilesPath (context), fileName);
    }
    
    public static boolean exists (Context context, String fileName) {
      return appFilesPath (context, fileName).exists ();
    }
    
    public static ArrayList<String> getExtras (Bundle bundle, String key) {
      return (bundle.getStringArrayList (key) == null ? new ArrayList<String> () : bundle.getStringArrayList (key));
    }
    
    public static void openUrl (Context context, URL url) {
      openUrl (context, url.toString ());
    }
    
    public static void openUrl (Context context, String url) {
      openUrl (context, Uri.parse (url));
    }
    
    public static void openUrl (Context context, Uri uri) {
      
      Intent intent = new Intent (Intent.ACTION_VIEW, uri);
      intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
      
      context.startActivity (intent);
      
    }
    
    public static String broadcastAction (Context context, String action, int id) {
      return context.getPackageName () + "." + action + "." + id;
    }
    
    public static Intent receiverIntent (Context context) {
      return receiverIntent (context, 1);
    }
    
    public static Intent receiverIntent (Context context, int id) {
      return new Intent (broadcastAction (context, "PackagesManager", id));
    }
    
    public static void registerReceiver (Context context, String permission, BroadcastReceiver receiver) {
      registerReceiver (context, permission, receiver, 1);
    }
    
    public static void registerReceiver (Context context, String permission, BroadcastReceiver receiver, int id) {
      
      IntentFilter intentFilter = new IntentFilter (broadcastAction (context, "PackagesManager", id));
      intentFilter.addAction (broadcastAction (context, "ACTION_INSTALL_COMMIT", id));
      
      context.registerReceiver (receiver, intentFilter, permission, null);
      
    }
    
    public static Map<String, String> releasesNames () {
      
      Map<String, String> output = new LinkedHashMap<> ();
      
      output.put ("1.0", "Apple Pie");
      
      output.put ("1.1", "Banana Bread");
      
      output.put ("1.5", "Cupcake");
      
      output.put ("1.6", "Donut");
      
      output.put ("2.0", "Eclair");
      output.put ("2.0.1", "Eclair");
      output.put ("2.1", "Eclair");
      
      output.put ("2.2", "Froyo");
      output.put ("2.2.1", "Froyo");
      output.put ("2.2.2", "Froyo");
      output.put ("2.2.3", "Froyo");
      
      output.put ("2.3", "Gingerbread");
      output.put ("2.3.1", "Gingerbread");
      output.put ("2.3.2", "Gingerbread");
      output.put ("2.3.3", "Gingerbread");
      output.put ("2.3.4", "Gingerbread");
      output.put ("2.3.5", "Gingerbread");
      output.put ("2.3.6", "Gingerbread");
      output.put ("2.3.7", "Gingerbread");
      
      output.put ("3.0", "Honeycomb");
      output.put ("3.0.1", "Honeycomb");
      output.put ("3.1", "Honeycomb");
      output.put ("3.2", "Honeycomb");
      output.put ("3.2.1", "Honeycomb");
      output.put ("3.2.2", "Honeycomb");
      
      output.put ("4.0.1", "Ice Cream Sandwich");
      output.put ("4.0.2", "Ice Cream Sandwich");
      output.put ("4.0.3", "Ice Cream Sandwich");
      output.put ("4.0.4", "Ice Cream Sandwich");
      
      output.put ("4.1.1", "Jelly Bean");
      output.put ("4.1.2", "Jelly Bean");
      output.put ("4.2", "Jelly Bean");
      output.put ("4.2.1", "Jelly Bean");
      output.put ("4.2.2", "Jelly Bean");
      output.put ("4.3", "Jelly Bean");
      output.put ("4.3.1", "Jelly Bean");
      
      output.put ("4.4", "KitKat");
      output.put ("4.4.1", "KitKat");
      output.put ("4.4.2", "KitKat");
      output.put ("4.4.3", "KitKat");
      output.put ("4.4.4", "KitKat");
      
      output.put ("5.0", "Lollipop");
      output.put ("5.0.1", "Lollipop");
      output.put ("5.0.2", "Lollipop");
      output.put ("5.1", "Lollipop");
      output.put ("5.1.1", "Lollipop");
      
      output.put ("6.0", "Marshmallow");
      output.put ("6.0.1", "Marshmallow");
      
      output.put ("7.0", "Nougat");
      output.put ("7.1", "Nougat");
      output.put ("7.1.1", "Nougat");
      output.put ("7.1.2", "Nougat");
      
      output.put ("8.0", "Oreo");
      
      return output;
      
    }
    
    public static Map<String, Integer> apiVersions () {
      
      Map<String, Integer> output = new LinkedHashMap<> ();
      
      output.put ("1.0", 1);
      
      output.put ("1.1", 2);
      
      output.put ("1.5", 3);
      
      output.put ("1.6", 4);
      
      output.put ("2.0", 5);
      
      output.put ("2.0.1", 6);
      
      output.put ("2.1", 7);
      
      output.put ("2.2", 8);
      output.put ("2.2.1", 8);
      output.put ("2.2.2", 8);
      output.put ("2.2.3", 8);
      
      output.put ("2.3", 9);
      output.put ("2.3.1", 9);
      output.put ("2.3.2", 9);
      
      output.put ("2.3.3", 10);
      output.put ("2.3.4", 10);
      output.put ("2.3.5", 10);
      output.put ("2.3.6", 10);
      output.put ("2.3.7", 10);
      
      output.put ("3.0", 11);
      
      output.put ("3.1", 12);
      
      output.put ("3.2", 13);
      output.put ("3.2.1", 13);
      output.put ("3.2.2", 13);
      output.put ("3.2.3", 13);
      output.put ("3.2.4", 13);
      output.put ("3.2.5", 13);
      output.put ("3.2.6", 13);
      
      output.put ("4.0", 14);
      output.put ("4.0.1", 14);
      output.put ("4.0.2", 14);
      
      output.put ("4.0.3", 15);
      output.put ("4.0.4", 15);
      
      output.put ("4.1", 16);
      output.put ("4.1.1", 16);
      output.put ("4.1.2", 16);
      
      output.put ("4.2", 17);
      output.put ("4.2.1", 17);
      output.put ("4.2.2", 17);
      
      output.put ("4.3", 18);
      output.put ("4.3.1", 18);
      
      output.put ("4.4", 19);
      output.put ("4.4.1", 19);
      output.put ("4.4.2", 19);
      output.put ("4.4.3", 19);
      output.put ("4.4.4", 19);
      
      output.put ("4.4W", 20);
      output.put ("4.4W.1", 20);
      output.put ("4.4W.2", 20);
      
      output.put ("5.0", 21);
      output.put ("5.0.1", 21);
      output.put ("5.0.2", 21);
      
      output.put ("5.1", 22);
      output.put ("5.1.1", 22);
      
      output.put ("6.0", 23);
      output.put ("6.0.1", 23);
      
      output.put ("7.0", 24);
      
      output.put ("7.1", 25);
      output.put ("7.1.2", 25);
      
      output.put ("8.0", 26);
      
      output.put ("8.1", 27);
      
      return output;
      
    }
    
    public static Notification.Builder notification (Context context, int title, String text, int largeIcon, int smallIcon) {
      return notification (context, context.getString (title), text, largeIcon, smallIcon, true);
    }
    
    public static Notification.Builder notification (Context context, int title, String text, int largeIcon, int smallIcon, boolean indeterminate) {
      return notification (context, context.getString (title), text, largeIcon, smallIcon, indeterminate, true);
    }
    
    public static Notification.Builder notification (Context context, String title, String text, int largeIcon, int smallIcon) {
      return notification (context, title, text, largeIcon, smallIcon, true);
    }
    
    public static Notification.Builder notification (Context context, String title, String text, int largeIcon, int smallIcon, boolean indeterminate) {
      return notification (context, title, text, largeIcon, smallIcon, indeterminate, true);
    }
    
    public static Notification.Builder notification (Context context, String title, String text, int largeIcon, int smallIcon, boolean indeterminate, boolean ongoing) {
      return new Notification.Builder (context).setOngoing (ongoing)
                                               .setContentTitle (title)
                                               .setContentText (text)
                                               .setLargeIcon (Graphic.toBitmap (context, largeIcon))
                                               .setSmallIcon (smallIcon)
                                               .setProgress (100, 0, indeterminate);
    }
    
    public static NotificationManager notify (Context context, Notification.Builder notification) {
      return notify (context, notification, 1);
    }
    
    public static NotificationManager notify (Context context, Notification.Builder notification, int id) {
      
      NotificationManager manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
      
      if (manager != null)
        manager.notify (id, notification.build ()); // TODO
      
      return manager;
      
    }
    
    public static Intent toIntent (Map<String, Object> data) throws JSONException {
      return toIntent (new Intent (), data);
    }
    
    public static Intent toIntent (Intent intent, Map<String, Object> data) {
      
      for (String key : data.keySet ()) {
        
        Object value = data.get (key);
        intent = intent (key, value, intent);
        
      }
      
      return intent;
      
    }
    
    public static Intent intent (String key, Object value, Intent intent) {
      
      if (value instanceof String)
        intent.putExtra (key, String.valueOf (value));
      else if (value instanceof Integer)
        intent.putExtra (key, (int) value);
      else if (value instanceof Long)
        intent.putExtra (key, (long) value);
      else if (value instanceof Boolean)
        intent.putExtra (key, (boolean) value);
      
      return intent;
      
    }
    
    public static Intent toIntent (SharedPreferences settings) {
      return toIntent (settings, new Intent ());
    }
    
    public static Intent toIntent (SharedPreferences settings, Intent intent) {
      
      Map<String, ?> entries = settings.getAll ();
      
      for (Map.Entry<String, ?> entry : entries.entrySet ())
        intent = intent (entry.getKey (), entry.getValue (), intent);
      
      return intent;
      
    }
    
    public static Bundle toBundle (SharedPreferences settings) {
      return toBundle (settings, new Intent ());
    }
    
    public static Bundle toBundle (SharedPreferences settings, Intent intent) {
      return toIntent (settings, intent).getExtras ();
    }
    
    public static Intent implode (Intent intent, Intent intent2) {
      return toIntent (intent, intent2.getExtras ());
    }
    
    public static Intent toIntent (Intent intent, Bundle bundle) {
      
      for (String key : bundle.keySet ()) {
        
        Object value = bundle.get (key);
        intent = intent (key, value, intent);
        
      }
      
      return intent;
      
    }
    
    public static Intent toIntent (JSONObject data) throws JSONException {
      return toIntent (data, new Intent ());
    }
    
    public static Intent toIntent (JSONObject data, Intent intent) throws JSONException {
      
      JSONArray keys = data.names ();
      
      for (int i = 0; i < Int.size (keys); ++i) {
        
        String key = keys.getString (i);
        Object value = data.get (key);
        
        intent = intent (key, value, intent);
        
      }
      
      return intent;
      
    }
    
    public static InputStream rawToInputStream (Context context, int id) {
      
      Resources resources = context.getResources ();
      return resources.openRawResource (id);
      
    }
    
    public static byte[] rawToByteArray (Context context, int id) throws IOException {
      return Arrays.toByteArray (rawToInputStream (context, id));
    }
    
    public static String rawToString (Context context, int id) throws IOException {
      return rawToString (context, id, Charset.forName (Strings.DEF_CHARSET));
    }
    
    public static String rawToString (Context context, int id, Charset encoding) throws IOException {
      return new String (rawToByteArray (context, id), encoding);
    }
    
    public static String getUserAgent (Context context, String name) {
      
      PackageData appData = new AppsManager (context).getPackageData ();
      
      Map<String, String> locale = Locales.getLocaleData ();
      Map<String, Object> info = Device.getInfo ();
      
      return name + "/" + appData.version + " (Android " + info.get ("release") + " " + info.get ("version_name") + " (" + info.get ("sdk") + "); " + locale.get (Locales.COUNTRY) + "; " + locale.get ("lang") + ") " + info.get ("brand") + " " + info.get ("model") + " " + info.get ("id");
      
    }
    
    public static JSONObject toJSONObject (SharedPreferences settings) throws JSONException {
      return toJSONObject (settings, new JSONObject ());
    }
    
    public static JSONObject toJSONObject (SharedPreferences settings, JSONObject data) throws JSONException {
      
      Map<String, ?> entries = settings.getAll ();
      
      for (Map.Entry<String, ?> entry : entries.entrySet ())
        data.put (entry.getKey (), entry.getValue ());
      
      return data;
      
    }
    
    public static void alert (Context context, int msg) {
      Toast.makeText (context, msg, Toast.LENGTH_SHORT).show ();
    }
    
    public static void alert (Context context, boolean msg) {
      Toast.makeText (context, String.valueOf (msg), Toast.LENGTH_SHORT).show ();
    }
    
    public static void alert (Context context, String[] msg) {
      alert (context, Arrays.implode (msg));
    }
    
    public static void alert (Context context, String[] msg, String sep) {
      alert (context, Arrays.implode (sep, msg));
    }
    
    public static void alert (Context context, List<?> msg) {
      alert (context, Arrays.toStringArray (msg), "\n");
    }
    
    public static void alert (Context context, Exception e) {
      alert (context, e.getMessage ());
    }
    
    public static void alert (Context context, String msg) {
      Toast.makeText (context, msg, Toast.LENGTH_SHORT).show ();
    }
    
    public static void alert (Context context, CharSequence msg) {
      Toast.makeText (context, msg, Toast.LENGTH_SHORT).show ();
    }
    
    public static void debug2 () {
      
      try {
        
        CharSequence result = Files.read (extStorageFile ("log.txt"));
        Files.write (result, extStorageFile ("log2.txt"), true);
        
      } catch (IOException | OutOfMemoryException e) {
        throw new RuntimeException (e);
      }
      
    }
    
    public static void debug (JSONArray... msg) {
      debug (System.debug (msg));
    }
    
    public static void debug (JSONObject... msg) {
      debug (System.debug (msg));
    }
    
    public static void debug (Intent msg) {
      debug (msg.getExtras ());
    }
    
    public static void debug (Bundle items) {
      
      List<String> msg = new ArrayList<> ();
      
      for (String key : items.keySet ())
        msg.add (key + ": " + items.get (key));
      
      debug (Arrays.implode (msg));
      
    }
    
    public static void debug (Object... msg) {
      debug (System.debug (msg));
    }
    
    public static void debug (List<?> msg) {
      debug (System.debug (msg));
    }
    
    public static void debug (ContentValues items) {
      
      List<String> msg = new ArrayList<> ();
      
      for (String key : items.keySet ())
        msg.add (key + ": " + items.get (key));
      
      debug (Arrays.implode (msg) + "\n");
      
    }
    
    public static void debug (String msg) {
      
      try {
        Files.write (msg, extStorageFile ("log.txt"), true);
      } catch (IOException e) {
        throw new RuntimeException ("Write permission denied. May be you forgot to add an android.permission.WRITE_EXTERNAL_STORAGE permission to your AndroidManifest.xml file.");
      }
      
    }
    
    public static String getAppDir (Context context) {
      return context.getApplicationInfo ().dataDir;
    }
    
    public static String extStorageDir () { // TODO: To File?
      return Environment.getExternalStorageDirectory ().getAbsolutePath ();
    }
    
    public static String extStorageDir (String... name) {
      return extStorageDir () + "/" + Arrays.implode ("/", name);
    }
    
    public static File extStorageFile (String... name) {
      return new File (extStorageDir (name));
    }
    
    public static String getExternalDir (Context context) {
      
      String externalFilesPath = getExternalFilesDir (context);
      return externalFilesPath.substring (0, externalFilesPath.lastIndexOf (context.getPackageName ()) - 1);
      
    }
    
    public static File getExternalDir (Context context, String appName) throws PackageManager.NameNotFoundException {
      
      PackageData appInfo = new AppsManager (context).getPackageData (appName);
      return getExternalDir (context, appInfo);
      
    }
    
    public static File getExternalDir (Context context, PackageData appInfo) {
      return new File (getExternalDir (context), new File (appInfo.appInfo.dataDir).getName ());
    }
    
    public static String getExternalFilesDir (Context context) {
      return getExternalFilesDir (context, false);
    }
    
    public static String getExternalFilesDir (Context context, boolean sdcard) {
      
      String folder;
      File file = context.getExternalFilesDir (null);
      
      if (file == null)
        folder = appFilesPath (context);
      else
        folder = file.getAbsolutePath ();
      
      if (sdcard) {
        
        String externalFile = "/storage/extSdCard/";
        
        if (new File (externalFile).exists ()) {
          
          folder = folder.replaceAll ("/storage/emulated/[0-9]+/", Strings.addEnd ("/", externalFile));
          folder = folder.replace ("/storage/sdcard/", Strings.addEnd ("/", externalFile));
          
        }
        
      } else {
        
        //if (folder.startsWith ("/storage/emulated/") && Build.VERSION.SDK_INT >= 18 && Build.VERSION.SDK_INT < 23)
        //  folder = folder.replace ("/storage/emulated/", "/mnt/shell/emulated/");
        
      }
      
      return folder;
      
    }
    
    public static File getExternalFilesDir (Context context, String appName) throws PackageManager.NameNotFoundException {
      
      PackageData appInfo = new AppsManager (context).getPackageData (appName);
      return getExternalFilesDir (context, appInfo);
      
    }
    
    public static File getExternalFilesDir (Context context, PackageData appInfo) {
      
      String dirPath = getExternalDir (context, appInfo).getAbsolutePath ();
      
      String externalFilesPath = getExternalFilesDir (context);
      externalFilesPath = externalFilesPath.substring (externalFilesPath.lastIndexOf (context.getPackageName ()) + Int.size (context.getPackageName ()));
      
      return new File (dirPath + externalFilesPath);
      
    }
    
    public static class Service {
      
      private Context context;
      private long startTime;
      
      private Intent intent;
      private PendingIntent pIntent;
      
      private AlarmManager alarmManager;
      
      public int type;
      
      public Service (Context context) {
        this.context = context;
      }
      
      public Service (Context context, long startTime) {
        
        this.context = context;
        this.startTime = startTime;
        
        type = AlarmManager.RTC_WAKEUP;
        
      }
      
      public Service setIntent (Class<?> service) {
        
        setIntent (new Intent (context, service));
        return this;
        
      }
      
      public Service setIntent (Intent intent) {
        
        this.intent = intent;
        return this;
        
      }
      
      public Service setIntent (PendingIntent intent) {
        
        pIntent = intent;
        return this;
        
      }
      
      public Service init () {
        
        if (pIntent == null && intent != null) setIntent (PendingIntent.getService (context, 0, intent, 0));
        alarmManager = (AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
        
        return this;
        
      }
      
      public Service start (long repeatTime) {
        
        if (alarmManager != null) alarmManager.setRepeating (type, startTime, repeatTime, pIntent);
        return this;
        
      }
      
      public void stop () {
        if (alarmManager != null) alarmManager.cancel (pIntent);
      }
      
    }
    
    public static Map<String, Object> getUriData (Context context, Uri uri) {
      
      Map<String, Object> result = new LinkedHashMap<> ();
      
      Cursor cursor = context.getContentResolver ().query (uri, null, null, null, null);
      
      if (cursor != null) {
        
        cursor.moveToFirst ();
        
        for (int i = 0; i < cursor.getColumnCount (); ++i) {
          
          String name = cursor.getColumnName (i);
          result.put (name, DB.get (name, cursor));
          
        }
        
        cursor.close ();
        
      }
      
      return result;
      
    }
    
    public static DataInputStream getAsset (Context context, String... file) throws IOException {
      
      AssetManager manager = context.getAssets ();
      
      if (manager != null)
        return new DataInputStream (manager.open (Arrays.implode ("/", file)));
      else
        throw new IOException ("AssetManager is null");
      
    }
    
  }