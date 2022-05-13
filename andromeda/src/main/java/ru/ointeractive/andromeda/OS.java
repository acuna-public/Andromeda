	package ru.ointeractive.andromeda;
	  /*
	   Created by Acuna on 17.07.2017
	  */
	
	import android.app.Activity;
	import android.content.BroadcastReceiver;
	import android.content.ContentValues;
	import android.content.Context;
	import android.content.Intent;
	import android.content.IntentFilter;
	import android.content.SharedPreferences;
	import android.content.pm.PackageManager;
	import android.content.res.Resources;
	import android.database.Cursor;
	import android.graphics.Bitmap;
	import android.net.Uri;
	import android.os.Build;
	import android.os.Bundle;
	import android.os.Environment;
	import android.provider.MediaStore;
	import android.widget.Toast;
	
	import java.io.ByteArrayOutputStream;
	import java.io.DataInputStream;
	import java.io.IOException;
	import java.io.OutputStream;
	import java.net.URL;
	import java.nio.charset.Charset;
	import java.util.LinkedHashMap;
	import java.util.Map;
	
	import ru.ointeractive.andromeda.apps.AppsManager;
	import ru.ointeractive.andromeda.apps.PackageData;
	import upl.core.Arrays;
	import upl.core.File;
	import upl.core.Int;
	import upl.core.Locales;
	import upl.core.System;
	import upl.core.exceptions.OutOfMemoryException;
	import upl.io.BufferedInputStream;
	import upl.io.InputStream;
	import upl.json.JSONArray;
	import upl.json.JSONException;
	import upl.json.JSONObject;
	import upl.util.ArrayList;
	import upl.util.List;
	
	public class OS {
		
		public static int SDK = Build.VERSION.SDK_INT;
		
		public static void share (Activity activity, String subject, String text, String title) {
			
			Intent intent = new Intent (Intent.ACTION_SEND);
			
			intent.setType ("text/*");
			
			intent.putExtra (Intent.EXTRA_SUBJECT, subject);
			intent.putExtra (Intent.EXTRA_TEXT, text);
			
			activity.startActivity (Intent.createChooser (intent, title));
			
		}
		
		public static void share (Activity activity, File file, String title) {
			
			Intent intent = new Intent (Intent.ACTION_SEND);
			
			intent.setType (new File (file.getName ()).getMimeType ());
			intent.putExtra (Intent.EXTRA_STREAM, Uri.fromFile (file));
			
			activity.startActivity (Intent.createChooser (intent, title));
			
		}
		
		public static void share (Activity activity, Bitmap bitmap, String title) throws IOException {
			
			Intent share = new Intent (Intent.ACTION_SEND);
			share.setType ("image/jpeg");
			
			ContentValues values = new ContentValues ();
			
			values.put (MediaStore.Images.Media.TITLE, "title");
			values.put (MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			
			Uri uri = activity.getContentResolver ().insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			
			OutputStream outstream = activity.getContentResolver ().openOutputStream (uri);
			
			bitmap.compress (Bitmap.CompressFormat.JPEG, 100, outstream);
			outstream.close ();
			
			share.putExtra (Intent.EXTRA_STREAM, uri);
			
			activity.startActivity (Intent.createChooser (share, title));
			
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
		
		public static java.util.ArrayList<?> getExtras (Bundle bundle, String key) {
			return (bundle.getStringArrayList (key) == null ? new java.util.ArrayList<> () : bundle.getStringArrayList (key));
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
			output.put ("8.1", "Oreo");
			
			output.put ("9", "Pie");
			
			output.put ("10", "Quince Tart");
			
			output.put ("11", "Red Velvet Cake");
			
			output.put ("12", "Snow Cone");
			
			return output;
			
		}
		
		public static Map<String, String> releasesNamesHypothetic () {
			
			Map<String, String> output = new LinkedHashMap<> ();
			
			output.put ("1.0", "Astroboy");
			
			output.put ("1.1", "Bender");
			
			output.put ("1.5", "Calculon");
			
			output.put ("1.6", "Donbot");
			
			output.put ("2.0", "Emotitron");
			output.put ("2.0.1", "Emotitron");
			output.put ("2.1", "Emotitron");
			
			output.put ("2.2", "Fabricio");
			output.put ("2.2.1", "Fabricio");
			output.put ("2.2.2", "Fabricio");
			output.put ("2.2.3", "Fabricio");
			
			output.put ("2.3", "Gearshift");
			output.put ("2.3.1", "Gearshift");
			output.put ("2.3.2", "Gearshift");
			output.put ("2.3.3", "Gearshift");
			output.put ("2.3.4", "Gearshift");
			output.put ("2.3.5", "Gearshift");
			output.put ("2.3.6", "Gearshift");
			output.put ("2.3.7", "Gearshift");
			
			output.put ("3.0", "Helper");
			output.put ("3.0.1", "Helper");
			output.put ("3.1", "Helper");
			output.put ("3.2", "Helper");
			output.put ("3.2.1", "Helper");
			output.put ("3.2.2", "Helper");
			
			output.put ("4.0.1", "IHawk");
			output.put ("4.0.2", "IHawk");
			output.put ("4.0.3", "IHawk");
			output.put ("4.0.4", "IHawk");
			
			output.put ("4.1.1", "Joey Mousepad");
			output.put ("4.1.2", "Joey Mousepad");
			output.put ("4.2", "Joey Mousepad");
			output.put ("4.2.1", "Joey Mousepad");
			output.put ("4.2.2", "Joey Mousepad");
			output.put ("4.3", "Joey Mousepad");
			output.put ("4.3.1", "Joey Mousepad");
			
			output.put ("4.4", "King Roberto");
			output.put ("4.4.1", "King Roberto");
			output.put ("4.4.2", "King Roberto");
			output.put ("4.4.3", "King Roberto");
			output.put ("4.4.4", "King Roberto");
			
			output.put ("5.0", "Leelabot");
			output.put ("5.0.1", "Leelabot");
			output.put ("5.0.2", "Leelabot");
			output.put ("5.1", "Leelabot");
			output.put ("5.1.1", "Leelabot");
			
			output.put ("6.0", "Mark 7G");
			output.put ("6.0.1", "Mark 7G");
			
			output.put ("7.0", "Norm");
			output.put ("7.1", "Norm");
			output.put ("7.1.1", "Norm");
			output.put ("7.1.2", "Norm");
			
			output.put ("8.0", "Oily");
			output.put ("8.1", "Oily");
			
			output.put ("9", "Paco");
			
			output.put ("10", "Q.T. McWhiskers");
			
			output.put ("11", "Rusty");
			
			output.put ("12", "Sinclair 2K");
			
			output.put ("13", "Tandy");
			
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
			
			output.put ("9", 28);
			
			output.put ("10", 29);
			
			output.put ("11", 30);
			
			output.put ("12", 31);
			
			return output;
			
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
		
		public static InputStream rawToInputStream (Context context, int id) throws IOException {
			
			Resources resources = context.getResources ();
			return new BufferedInputStream (resources.openRawResource (id));
			
		}
		
		public static byte[] rawToByteArray (Context context, int id) throws IOException {
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
			rawToInputStream (context, id).copy (buffer);
			
			return buffer.toByteArray ();
			
		}
		
		public static String rawToString (Context context, int id) throws IOException {
			return rawToString (context, id, Charset.forName (upl.type.String.DEF_CHARSET));
		}
		
		public static String rawToString (Context context, int id, Charset encoding) throws IOException {
			
			if (OS.SDK >= 9)
				return new String (rawToByteArray (context, id), encoding);
			else
				return new String (rawToByteArray (context, id));
			
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
		
		public static void alert (Context context, Object[] msg) {
			alert (context, Arrays.implode (msg));
		}
		
		public static void alert (Context context, Object[] msg, String sep) {
			alert (context, Arrays.implode (sep, msg));
		}
		
		public static void alert (Context context, List<?> msg) {
			alert (context, msg.toArray (new Object[0]), "\n");
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
				
				CharSequence result = extStorageFile ("log.txt").read ();
				extStorageFile ("log2.txt").write (result, true);
				
			} catch (IOException | OutOfMemoryException e) {
				throw new RuntimeException (e);
			}
			
		}
		
		public static void debug (Object... msg) {
			debug (Arrays.implode (" - ", msg));
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
			
			debug (msg.implode ());
			
		}
		
		public static void debug (ContentValues items) {
			
			List<String> msg = new ArrayList<> ();
			
			for (String key : items.keySet ()) // TODO
				msg.add (key + ": " + items.get (key));
			
			debug (msg.implode () + "\n");
			
		}
		
		public static void debug (String msg) {
			
			try {
				extStorageFile ("log.txt").write (msg, true);
			} catch (IOException e) {
				throw new RuntimeException ("Write permission denied. Maybe you forgot to add an android.permission.WRITE_EXTERNAL_STORAGE permission to your AndroidManifest.xml file.");
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
			
			java.io.File file = (OS.SDK >= 8 ? context.getExternalFilesDir (null) : Environment.getExternalStorageDirectory ()); // SUPPORT 8
			
			if (file == null)
				folder = appFilesPath (context);
			else
				folder = file.getAbsolutePath ();
			
			if (sdcard) {
				
				String externalFile = "/storage/extSdCard/";
				
				if (new File (externalFile).exists ()) {
					
					folder = folder.replaceAll ("/storage/emulated/[0-9]+/", new upl.type.String (externalFile).addEnd ("/"));
					folder = folder.replace ("/storage/sdcard/", new upl.type.String (externalFile).addEnd ("/"));
					
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
		
		public static InputStream getAsset (Context context, String... file) throws IOException {
			return new BufferedInputStream (new DataInputStream (context.getAssets ().open (Arrays.implode ("/", file))));
		}
		
	}