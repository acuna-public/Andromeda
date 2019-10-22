  package pro.acuna.andromeda;
  /*
   Created by Acuna on 28.09.2017
  */
  
  /*
    1.1  23.02.2018
    
    Добавлена возможность бекапа телефонной книги
    
    1.2  28.06.2018
    
    Добавлена поддержка провайдеров
    
  */
  
  import android.content.ContentResolver;
  import android.content.Context;
  import android.content.pm.PackageInfo;
  import android.content.pm.PackageManager;
  
  import java.io.File;
  import java.io.IOException;
  import java.util.ArrayList;
  import java.util.HashMap;
  import java.util.List;
  import java.util.Map;
  
  import pro.acuna.andromeda.providers.Backups;
  import pro.acuna.andromeda.providers.BackupsApps;
  import pro.acuna.andromeda.providers.BackupsAppsData;
  import pro.acuna.andromeda.providers.BackupsAppsDataExternal;
  import pro.acuna.andromeda.providers.BackupsContacts;
  import pro.acuna.archiver.Archiver;
  import pro.acuna.jabadaba.Arrays;
  import pro.acuna.jabadaba.Console;
  import pro.acuna.jabadaba.Files;
  
  public class BackupsManager extends Backups {
    
    public static final String TYPE_USER_DICTIONARY = "user_dictionary";
    public static final String TYPE_WIFI_PASSWORDS = "wifi_passwords";
    
    protected Context context;
    private String prefPassword = "", cryptoAlgo;
    private BackupListener listener;
    private List<PackageData> appsInfo = new ArrayList<> ();
    private List<String> actions = new ArrayList<> ();
    private List<File> delFiles = new ArrayList<> (), delFolders = new ArrayList<> ();
    public boolean symlinks = false, delete = true;
    protected File backupDir;
    private File backupFile;
    protected File backupsPath;
    private String[] denyFolders;
    protected ContentResolver cr;
    protected String followSymlinks, action;
    
    protected AppsManager apps;
    protected Archiver archieve;
    protected Console exec;
    
    public BackupsManager () {}
    
    public BackupsManager (Context context) {
      
      this.context = context;
      
      denyFolders = new String[] {"lib"};
      
      apps = new AppsManager (context);
      
      archieve = new Archiver ();
      archieve.setShell (Console.su);
      
      cr = context.getContentResolver ();
      
      setPlugin (new BackupsApps ());
      setPlugin (new BackupsAppsData ());
      setPlugin (new BackupsAppsDataExternal ());
      setPlugin (new BackupsContacts ());
      
    }
    
    public BackupsManager (BackupsManager manager) throws BackupException {
      
      context = manager.context;
      backupsPath = manager.backupsPath;
      appInfo = manager.appInfo;
      exec = manager.exec;
      apps = manager.apps;
      archieve = manager.archieve;
      followSymlinks = manager.followSymlinks;
      
    }
    
    public BackupsManager setBackupFile (String file) throws BackupException {
      return setBackupFile (new File (file));
    }
    
    public BackupsManager setBackupFile (File file) throws BackupException {
      
      backupFile = file;
      
      try {
        
        archieve.setPassword (prefPassword);
        archieve.setCryptoAlgoritm (cryptoAlgo);
        
        archieve.addListener (new Archiver.ArchievsListener () {
          
          @Override
          public void onProgress (String file, long i, long total) {
            if (listener != null) listener.onProgress (file, i, total);
          }
          
          @Override
          public void onError (String line) {
            if (listener != null) listener.onArchieveError (line);
          }
          
        });
        
        exec = new Console ();
        
        exec.shell (Console.su);
        exec.open ();
        
      } catch (Console.ConsoleException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
    public File getBackupFile () {
      return backupFile;
    }
    
    public BackupsManager create () throws BackupException {
      
      try {
        archieve.create (backupFile);
      } catch (Archiver.CompressException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
    public static class BackupException extends Exception {
      
      public BackupException (Exception e) {
        super (e);
      }
      
      @Override
      public Exception getCause () {
        return (Exception) super.getCause ();
      }
      
    }
    
    public interface BackupListener {
      
      void onError (String line);
      void onProgress (String file, long i, long total);
      void onArchieveError (String line);
      void onExecute (String line, int i);
      void onFinish ();
      
    }
    
    public BackupsManager setAction (String action) {
      
      actions.add (action);
      return this;
      
    }
    
    public BackupsManager setDelete (boolean delete) {
      
      this.delete = delete;
      return this;
      
    }
    
    public BackupsManager setAppInfo (PackageInfo appInfo) {
      
      setAppInfo (apps.getPackageData (appInfo));
      return this;
      
    }
    
    public BackupsManager setAppInfo (String appName) throws BackupException {
      
      try {
        setAppInfo (apps.getPackageData (appName));
      } catch (PackageManager.NameNotFoundException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
    public BackupsManager setAppInfo (List<PackageData> appInfo) {
      
      this.appsInfo = appInfo;
      return this;
      
    }
    
    public BackupsManager setAppInfo (PackageData appInfo) {
      
      appsInfo.clear ();
      appsInfo.add (appInfo);
      
      return this;
      
    }
    
    public BackupsManager setSrcFolder (String file) {
      return setSrcFolder (new File (file));
    }
    
    public BackupsManager setSrcFolder (File file) {
      
      backupDir = file;
      return this;
      
    }
    
    public BackupsManager setListener (BackupListener listener) {
      
      this.listener = listener;
      return this;
      
    }
    
    public BackupsManager setPassword (String password) {
      
      prefPassword = password;
      return this;
      
    }
    
    public BackupsManager setCryptoAlgoritm (String algo) {
      
      cryptoAlgo = algo;
      return this;
      
    }
    
    public BackupsManager setPath (String path) {
      
      archieve.setPath (path);
      return this;
      
    }
    
    private List<Backups> plugins = new ArrayList<> ();
    
    public BackupsManager setPlugin (Backups plugin) {
      
      plugins.add (plugin);
      return this;
      
    }
    
    private Backups plugin;
    
    @Override
    public Backups newInstance (BackupsManager manager) throws BackupException {
      return plugin.newInstance (manager);
    }
    
    @Override
    public String[] setActions () {
      return plugin.setActions ();
    }
    
    @Override
    public Object[] setPluginItem () {
      return (Arrays.contains (action, items) ? items.get (action) : plugin.setPluginItem ());
    }
    
    private Map<String, Object[]> items = new HashMap<> ();
    
    public BackupsManager setProviderItem (String type, Object[] item) {
      
      items.put (type, item);
      return this;
      
    }
    
    protected PackageData appInfo;
    
    @Override
    public BackupsManager doBackup () throws BackupException {
      
      try {
        
        for (Backups plugin : plugins)
          for (String action : plugin.setActions ())
            if (Arrays.contains (action, actions)) {
          
          this.plugin = plugin;
          
          followSymlinks = (symlinks ? "L" : "");
          
          for (PackageData appInfo : appsInfo) {
            
            try {
              
              if (appInfo.appInfo.dataDir != null) {
                
                this.appInfo = appInfo;
                
                backupsPath = new File (backupDir, action + "/" + appInfo.packageName);
                Files.makeDir (backupsPath);
                
                delFolders.add (backupsPath);
                
                exec.addListener (new Console.Listener () {
                  
                  @Override
                  public void onExecute (String line, int i) {
                    if (listener != null) listener.onExecute (line, i);
                  }
                  
                  @Override
                  public void onSuccess (String line, int i) {}
                  
                  @Override
                  public void onError (String line, int i) {
                    
                    if (
                      listener != null &&
                      (
                        (!symlinks
                        && (
                          line.contains ("lib")
                          && (
                               (line.contains ("not permitted") && line.contains ("symlink"))
                               ||
                               line.contains ("No such file or directory")
                            )
                          )
                        )
                        || (line.contains ("mozilla") && line.contains ("/lock"))
                        || line.endsWith ("Operation not permitted")
                      )
                    ) listener.onError (line);
                    
                  }
                  
                });
                
                plugin.newInstance (this).doBackup ();
                
              } else throw new IOException ("dataDir is null. This is unexpected, please report it.");
              
            } catch (IOException e) {
              throw new BackupException (e);
            }
            
          }
          
        }
        
        exec.process ();
        
        archieve.pack ();
        archieve.close ();
        
        if (listener != null) listener.onFinish ();
        
        return this;
        
      } catch (Console.ConsoleException | Archiver.CompressException e) {
        throw new BackupException (e);
      }
      
    }
    
    public BackupsManager doRestore () throws BackupException {
      
      try {
        
        archieve.open (backupFile);
        archieve.unpack (backupDir);
        
        for (Backups plugin : plugins)
          for (String action : plugin.setActions ())
            if (Arrays.contains (action, actions)) {
          
          this.plugin = plugin;
          
          for (PackageData appInfo : appsInfo) {
            
            try {
              
              this.appInfo = appInfo;
              
              backupsPath = new File (backupDir, action + "/" + appInfo.packageName);
              Files.makeDir (backupsPath);
              
              exec.addListener (new Console.Listener () {
                
                @Override
                public void onExecute (String line, int i) {
                  if (listener != null) listener.onExecute (line, i);
                }
                
                @Override
                public void onSuccess (String line, int i) {}
                
                @Override
                public void onError (String line, int i) {
                  
                  if (listener != null) {
                    
                    String[] data = AppsService.failureData (line);
                    
                    if (data[1] != null)
                      listener.onError (data[1]);
                    
                  }
                  
                }
                
              });
              
              plugin.newInstance (this).doRestore ();
              
            } catch (IOException e) {
              throw new BackupException (e);
            }
            
          }
          
          delFolders.add (backupsPath);
          
        }
        
        exec.add (archieve.getCommands ());
        
        exec.process ();
        archieve.clear ();
        
        return this;
        
      } catch (Console.ConsoleException | Archiver.DecompressException e) {
        throw new BackupException (e);
      }
      
    }
    
    public BackupsManager clean () throws BackupException {
      
      try {
        
        if (delete) {
          
          for (File folder : delFolders)
            Files.delete (folder);
          
          for (File file : delFiles)
            Files.delete (file);
          
        }
        
        if (exec != null) exec.close ();
        
      } catch (IOException | Console.ConsoleException e) {
        throw new BackupException (e);
      }
      
      return this;
      
    }
    
  }