	package pro.acuna.andromeda.providers;
	/*
	 Created by Acuna on 28.06.2018
	*/
	
	import android.content.pm.PackageManager;
	
	import java.io.File;
	
	import pro.acuna.andromeda.BackupsManager;
	import pro.acuna.andromeda.OS;
	import pro.acuna.jabadaba.Console;
	import pro.acuna.jabadaba.Int;
	
	public class BackupsAppsDataExternal extends BackupsManager {
		
		public static final String ACTION = "apps_data_external";
		
		public BackupsAppsDataExternal () {}
		
		private BackupsAppsDataExternal (BackupsManager manager) throws BackupException {
			super (manager);
		}
		
		@Override
		public Backups newInstance (BackupsManager manager) throws BackupException {
			return new BackupsAppsDataExternal (manager);
		}
		
		@Override
		public String[] setActions () {
			return new String[] {ACTION};
		}
		
		@Override
		public Object[] setPluginItem () {
			return new Object[0];
		}
		
		@Override
		public BackupsManager doBackup () throws BackupException {
			
			try {
				
				File externalDataDir = OS.getExternalDir (context, appInfo.packageName);
				
				if (externalDataDir.exists ()) {
					
					exec.add ("cp -R" + followSymlinks + " " + externalDataDir + "/* " + backupsPath);
					
					archieve.addPath (externalDataDir.getAbsolutePath ());
					archieve.addFolder (backupsPath);
					
				}
				
				return this;
				
			} catch (Console.ConsoleException | PackageManager.NameNotFoundException e) {
				throw new BackupException (e);
			}
			
		}
		
		@Override
		public BackupsManager doRestore () throws BackupException {
			
			try {
				
				if (backupsPath.exists ()) {
					
					File externalDataDir = OS.getExternalDir (context, appInfo.packageName);
					
					if (!externalDataDir.exists ())
						exec.add ("mkdir " + externalDataDir);
					
					if (Int.size (backupsPath.list ()) > 0)
						exec.add ("cp -r " + backupsPath + "/* " + externalDataDir);
					
				}
				
				return this;
				
			} catch (PackageManager.NameNotFoundException | Console.ConsoleException e) {
				throw new BackupException (e);
			}
			
		}
		
	}