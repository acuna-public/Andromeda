	package pro.acuna.andromeda.providers;
	/*
	 Created by Acuna on 28.06.2018
	*/
	
	import android.Manifest;
	import android.database.Cursor;
	import android.net.Uri;
	import android.provider.ContactsContract;
	
	import org.json.JSONArray;
	import org.json.JSONException;
	
	import java.io.File;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.Map;
	
	import pro.acuna.andromeda.Arrays;
	import pro.acuna.andromeda.BackupsManager;
	import pro.acuna.jabadaba.Files;
	
	public class BackupsContacts extends BackupsManager {
		
		private Map<String, Uri> uris = new HashMap<> ();
		
		private static final String TYPE_CONTACTS = "contacts";
		private static final String TYPE_SMS = "sms";
		private static final String TYPE_CALLS = "calls";
		
		public BackupsContacts () {}
		
		private BackupsContacts (BackupsManager manager) throws BackupException {
			
			super (manager);
			
			uris.put (TYPE_CONTACTS, ContactsContract.Contacts.CONTENT_URI);
			
		}
		
		@Override
		public Backups newInstance (BackupsManager manager) throws BackupException {
			return new BackupsContacts (manager);
		}
		
		@Override
		public String[] setActions () {
			return new String[] { TYPE_CONTACTS, TYPE_SMS, TYPE_CALLS };
		}
		
		@Override
		public Object[] setPluginItem () {
			return new Object[0];
		}
		
		private Cursor getCursor (Uri uri) {
			return cr.query (uri, null, null, null, null);
		}
		
		@Override
		public BackupsManager doBackup () throws BackupException {
			
			Map<String, String> perm = new HashMap<> ();
			
			perm.put (TYPE_SMS, Manifest.permission.READ_SMS);
			perm.put (TYPE_CONTACTS, Manifest.permission.READ_CONTACTS);
			perm.put (TYPE_CALLS, "android.permission.READ_CALL_LOG");
			
			if ((apps.hasPermission (perm.get (action)) || perm.get (action) == null) && uris.get (action) != null) {
				
				JSONArray array;
				Cursor cursor = getCursor (uris.get (action));
				
				if (cursor != null) {
					
					try {
						
						if (action.equals (TYPE_CONTACTS)) {
							
							array = new JSONArray ();
							array.put (Arrays.toJSONArray (cursor));
							
							Cursor pCur = getCursor (ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
							array.put (Arrays.toJSONArray (pCur));
							
							pCur.close ();
							
						} else array = Arrays.toJSONArray (cursor);
						
						File file = new File (backupDir, action + ".json");
						Files.write (array, file);
						
						archieve.add (file);
						
					} catch (JSONException | IOException e) {
						throw new BackupException (e);
					}
					
					cursor.close ();
					
				}
				
			}
			
			return this;
			
		}
		
		@Override
		public BackupsManager doRestore () throws BackupException {
			
			Map<String, String> perm = new HashMap<> ();
			
			perm.put (TYPE_CONTACTS, Manifest.permission.WRITE_CONTACTS);
			perm.put (TYPE_CALLS, "android.permission.WRITE_CALL_LOG");
			
			return this;
			
		}
		
	}