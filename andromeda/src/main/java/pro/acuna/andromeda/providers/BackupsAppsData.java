	package pro.acuna.andromeda.providers;
	/*
	 Created by Acuna on 28.06.2018
	*/
	
	import android.os.Build;
	
	import java.io.File;
	
	import pro.acuna.andromeda.BackupsManager;
	import pro.acuna.jabadaba.Console;
	import pro.acuna.jabadaba.Int;
	
	public class BackupsAppsData extends BackupsManager {
		
		public static final String ACTION = "apps_data";
		
		public BackupsAppsData () {}
		
		private BackupsAppsData (BackupsManager manager) throws BackupException {
			super (manager);
		}
		
		@Override
		public Backups newInstance (BackupsManager manager) throws BackupException {
			return new BackupsAppsData (manager);
		}
		
		@Override
		public String[] setActions () {
			return new String[] { ACTION };
		}
		
		@Override
		public Object[] setPluginItem () {
			return new Object[0];
		}
		
		@Override
		public BackupsManager doBackup () throws BackupException {
			
			try {
				
				exec.add ("cp -R" + followSymlinks + " " + appInfo.appInfo.dataDir + "/* " + backupsPath);
				
				archieve.addPath (appInfo.appInfo.dataDir);
				archieve.addFolder (backupsPath);
				
				return this;
				
			} catch (Console.ConsoleException e) {
				throw new BackupException (e);
			}
			
		}
		
		@Override
		public BackupsManager doRestore () throws BackupException {
			
			try {
				
				if (!new File (appInfo.appInfo.dataDir).exists ())
					exec.add ("mkdir " + appInfo.appInfo.dataDir);
				
				if (Int.size (backupsPath.list ()) > 0) {
					
					exec.add ("cp -r " + backupsPath + "/* " + appInfo.appInfo.dataDir);
					
					if (Build.VERSION.SDK_INT >= 23)
						exec.add ("restorecon -R " + appInfo.appInfo.dataDir);
					
				}
				
			} catch (Console.ConsoleException e) {
				throw new BackupException (e);
			}
			
			return this;
			
		}
		
	}