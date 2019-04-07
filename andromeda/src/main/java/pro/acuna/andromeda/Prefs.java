	package pro.acuna.andromeda;
	/*
	 Created by Acuna on 28.04.2018
	*/
	
	import android.content.Context;
	import android.content.SharedPreferences;
	import android.graphics.Typeface;
	
	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.util.HashMap;
	import java.util.LinkedHashMap;
	import java.util.Map;
	import java.util.Set;
	
	import pro.acuna.jabadaba.Arrays;
	
	public class Prefs implements SharedPreferences {
		
		public SharedPreferences prefs;
		public SharedPreferences.Editor editor;
		
		private Map<String, Object> defPrefs;
		
		public Prefs (Context context, String name) {
			this (context, name, new HashMap<String, Object> ());
		}
		
		public Prefs (Context context, String name, Map<String, Object> defPrefs) {
			this (context, name, defPrefs, Context.MODE_PRIVATE);
		}
		
		public Prefs (Context context, String name, Map<String, Object> defPrefs, int mode) {
			
			prefs = context.getSharedPreferences (name, mode);
			editor = edit ();
			
			this.defPrefs = defPrefs;
			
		}
		
		public Prefs setDefPrefs (Map<String, Object> defPrefs) {
			
			for (String key : defPrefs.keySet ())
				this.defPrefs.put (key, defPrefs.get (key));
			
			return this;
			
		}
		
		private Object defValue (String key, Object defVal) {
			
			Object value = defPrefs.get (key);
			if (value == null) value = defVal;
			
			return value;
			
		}
		
		public String getString (String key) {
			return getString (key, defValue (key, "").toString ());
		}
		
		public Map<String, Typeface> getTypefaces () {
			
			Map<String, Typeface> typefaces = new LinkedHashMap<> ();
			
			typefaces.put ("serif", Typeface.SERIF);
			typefaces.put ("sans_serif", Typeface.SANS_SERIF);
			typefaces.put ("monospace", Typeface.MONOSPACE);
			typefaces.put ("default", Typeface.DEFAULT);
			typefaces.put ("default_bold", Typeface.DEFAULT_BOLD);
			
			return typefaces;
			
		}
		
		public Typeface getFontFamily (String key, Typeface defVal) {
			
			Map<String, Typeface> typefaces = getTypefaces ();
			return typefaces.get (getString (key, Arrays.getKey (defValue (key, defVal), typefaces).toString ()));
			
		}
		
		public String get (String key, String value) {
			return getString (key, value);
		}
		
		@Override
		public String getString (String key, String value) {
			return prefs.getString (key, value);
		}
		
		public int getInt (String key) {
			return getInt (key, (int) defValue (key, 0));
		}
		
		public int get (String key, int value) {
			return getInt (key, value);
		}
		
		@Override
		public int getInt (String key, int value) {
			return prefs.getInt (key, value);
		}
		
		public long getLong (String key) {
			return getLong (key, (long) defValue (key, (long) 0));
		}
		
		public long get (String key, long value) {
			return getLong (key, value);
		}
		
		@Override
		public long getLong (String key, long value) {
			return prefs.getLong (key, value);
		}
		
		public float get (String key, float value) {
			return getFloat (key, value);
		}
		
		public float getFloat (String key) {
			return getFloat (key, (float) defValue (key, 0f));
		}
		
		@Override
		public float getFloat (String key, float value) {
			return prefs.getFloat (key, value);
		}
		
		public boolean getBool (String key) {
			return getBoolean (key, (boolean) defValue (key, false));
		}
		
		public boolean get (String key, boolean value) {
			return getBoolean (key, value);
		}
		
		@Override
		public boolean getBoolean (String key, boolean value) {
			return prefs.getBoolean (key, value);
		}
		
		@Override
		public Set<String> getStringSet (String key, Set<String> value) {
			return prefs.getStringSet (key, value);
		}
		
		public Set<String> get (String key, Set<String> value) {
			return getStringSet (key, value);
		}
		
		public boolean get (String key, Map<String, Boolean> items) {
			return get (key, items.get (key));
		}
		
		public int getColor (String key) {
			return getColor (key, defValue (key, null).toString ());
		}
		
		public int getColor (String key, String defColor) {
			return Color.valueOf (get (key, defColor));
		}
		
		/*public JSONArray getJSONArray (String key) throws JSONException {
			return new JSONArray (getString (key, defValue (key, new JSONArray ()).toString ()));
		}*/
		
		public JSONArray get (String key, JSONArray value) throws JSONException {
			return new JSONArray (get (key, value.toString ()));
		}
		
		public JSONObject get (String key, JSONObject value) throws JSONException {
			return new JSONObject (get (key, value.toString ()));
		}
		
		public String get (String key, String defValue, boolean decrypt) throws Crypto.DecryptException {
			
			String value = getString (key, defValue);
			if (decrypt && !value.equals (defValue)) value = Crypto.decrypt (value);
			
			return value;
			
		}
		
		@Override
		public SharedPreferences.Editor edit () {
			return prefs.edit ();
		}
		
		public void put (String key, Object value) {
			
			try {
				put (key, value, false);
			} catch (Crypto.EncryptException e) {
				// empty
			}
			
		}
		
		public void set (String key, Object value) {
			
			try {
				set (key, value, false);
			} catch (Crypto.EncryptException e) {
				// empty
			}
			
		}
		
		public void put (String key, Object value, boolean encrypt) throws Crypto.EncryptException {
			
			if (value instanceof Integer)
				editor.putInt (key, (int) value);
			else if (value instanceof Long)
				editor.putLong (key, (long) value);
			else if (value instanceof Boolean)
				editor.putBoolean (key, (boolean) value);
			else {
				
				String str = String.valueOf (value);
				
				if (encrypt) str = Crypto.encrypt (str);
				editor.putString (key, str);
				
			}
			
		}
		
		public void set (Map<?, ?> value) {
			
			try {
				set (value, false);
			} catch (Crypto.EncryptException e) {
				// empty
			}
			
		}
		
		public void set (Map<?, ?> value, boolean encrypt) throws Crypto.EncryptException {
			
			for (Object key : value.keySet ())
				put (key.toString (), value, encrypt);
			
			apply ();
			
		}
		
		public void set (String key, Object value, boolean encrypt) throws Crypto.EncryptException {
			
			put (key, value, encrypt);
			apply ();
			
		}
		
		public void apply () {
			editor.apply ();
		}
		
		public void setColor (String key, int color) {
			set (key, Color.toString (color));
		}
		
		public void setFontFamily (String key, String value) {
			set (key, value);
		}
		
		@Override
		public void registerOnSharedPreferenceChangeListener (OnSharedPreferenceChangeListener listener) {
			prefs.registerOnSharedPreferenceChangeListener (listener);
		}
		
		@Override
		public void unregisterOnSharedPreferenceChangeListener (OnSharedPreferenceChangeListener listener) {
			prefs.unregisterOnSharedPreferenceChangeListener (listener);
		}
		
		@Override
		public boolean contains (String string) {
			return prefs.contains (string);
		}
		
		@Override
		public Map<String, ?> getAll () {
			return prefs.getAll ();
		}
		
	}