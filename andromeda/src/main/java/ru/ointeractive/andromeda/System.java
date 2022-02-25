	package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 17.07.2017
  */
	
	import android.app.ActivityManager;
	import android.content.Context;
	import android.content.pm.PackageManager;
	import android.os.Process;
	import android.support.annotation.NonNull;
	
	import java.io.IOException;
	
	import ru.ointeractive.andromeda.apps.AppsManager;
	import ru.ointeractive.andromeda.apps.PackageData;
	import upl.core.Console;
	import upl.core.File;
	import upl.core.Int;
	import upl.core.exceptions.ConsoleException;
	import upl.util.ArrayList;
	import upl.util.List;
	
	public class System {
		
		public static boolean debug = false;
		
		public static java.util.List<ActivityManager.RunningAppProcessInfo> processList (Context context) {
			
			ActivityManager activityManager = (ActivityManager) context.getSystemService (Context.ACTIVITY_SERVICE);
			return (activityManager != null ? activityManager.getRunningAppProcesses () : new ArrayList<ActivityManager.RunningAppProcessInfo> ());
			
		}
		
		public static upl.util.List<String> remount (String file, String mountType, upl.util.List<String> cmds) throws SystemException {
			
			try {
				
				mountType = mountType.toLowerCase ();
				List<String[]> mountPoints = upl.core.System.getMountPoint (file);
				
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
				
			} catch (ConsoleException e) {
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
					throw new IOException ("Can't remount " + file + ": " + output.implode ());
				
				return upl.core.System.checkMountPoint (file);
				
			} catch (IOException | ConsoleException e) {
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
		
		public static void killProcess (Context context, String appName) throws ConsoleException, PackageManager.NameNotFoundException {
			killProcess (context, new String[] {appName});
		}
		
		public static void killProcess (Context context, String[] appNames) throws ConsoleException, PackageManager.NameNotFoundException {
			
			PackageData pInfo;
			String[] names = new String[Int.size (appNames)];
			
			for (int i = 0; i < Int.size (appNames); ++i) {
				
				pInfo = new AppsManager (context).getPackageData (appNames[i]);
				names[i] = pInfo.appInfo.processName;
				
			}
			
			upl.core.System.killProcess (names, new ArrayList<String> ());
			
		}
		
		public static List<String> killProcess (PackageData pInfo, List<String> cmds) throws ConsoleException {
			return upl.core.System.killProcess (pInfo.appInfo.processName, cmds);
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
			
			new File (logFile (context, file)).writeLog (e, appName);
			return (full ? e.toString () : e.getMessage ());
			
		}
		
		public static void writeLog (Context context, String text, String file) {
			new File (logFile (context, file)).writeLog (text);
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
		
		public static String error (Context context, Throwable e, Object appName, String file) {
			return error (context, e, appName, file, false);
		}
		
		public static String error (Context context, Throwable e, Object appName, String file, boolean full) {
			
			new File (logFile (context, file)).writeLog (e, appName);
			return (full ? e.toString () : e.getMessage ());
			
		}
		
		public static String error (Context context, List<?> items) {
			return error (context, items.implode ());
		}
		
		public static String error (Context context, String mess) {
			return error (context, mess, "error");
		}
		
		public static String error (Context context, String mess, String file) {
			
			new File (logFile (context, file)).writeLog (File.logText (mess));
			return mess;
			
		}
		
		public static void killProcess () {
			
			Process.killProcess (Process.myPid ());
			java.lang.System.exit (10);
			
		}
		
		public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
			
			private Context context;
			
			public ExceptionHandler (Context context) {
				this.context = context;
			}
			
			@Override
			public void uncaughtException (@NonNull Thread t, @NonNull Throwable e) {
				
				error (context, e, "", "crash");
				Thread.getDefaultUncaughtExceptionHandler ().uncaughtException (t, e);
				
			}
			
		}
		
	}