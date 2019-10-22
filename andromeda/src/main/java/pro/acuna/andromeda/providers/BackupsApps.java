  package pro.acuna.andromeda.providers;
  /*
   Created by Acuna on 28.06.2018
  */
  
  import java.io.File;
  import java.util.ArrayList;
  
  import pro.acuna.andromeda.AppsService;
  import pro.acuna.andromeda.BackupsManager;
  import pro.acuna.jabadaba.Console;
  
  public class BackupsApps extends BackupsManager {
    
    public static final String ACTION = "apps";
    
    public BackupsApps () {}
    
    private BackupsApps (BackupsManager manager) throws BackupException {
      super (manager);
    }
    
    @Override
    public Backups newInstance (BackupsManager manager) throws BackupException {
      return new BackupsApps (manager);
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
        
        File file = new File (backupsPath, appInfo.packageName + ".apk");
        exec.add (apps.extractApp (appInfo.appInfo.sourceDir, file, new ArrayList<String> ()));
        
        archieve.add (file);
        
      } catch (Console.ConsoleException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
    @Override
    public BackupsManager doRestore () throws BackupException {
      
      try {
        exec.add (AppsService.install (new File (backupsPath, appInfo.packageName + ".apk"), new ArrayList<String> ()));
      } catch (Console.ConsoleException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
  }