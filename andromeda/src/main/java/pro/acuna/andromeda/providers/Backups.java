  package pro.acuna.andromeda.providers;
  /*
   Created by Acuna on 28.06.2018
  */
  
  import pro.acuna.andromeda.BackupsManager;
  
  public abstract class Backups {
    
    public abstract Backups newInstance (BackupsManager manager) throws BackupsManager.BackupException;
    public abstract String[] setActions ();
    public abstract Object[] setPluginItem ();
    public abstract BackupsManager doBackup () throws BackupsManager.BackupException;
    public abstract BackupsManager doRestore () throws BackupsManager.BackupException;
    
  }