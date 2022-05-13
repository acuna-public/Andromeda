	package ru.ointeractive.andromeda;
  /*
   Created by Acuna on 28.04.2018
  */
	
	import android.content.Context;
	import android.content.SharedPreferences;
	import android.graphics.Typeface;
	
	import ru.ointeractive.andromeda.graphic.Color;
	import upl.core.adapter.Preferences;
	import upl.crypto.exceptions.DecryptException;
	import upl.crypto.exceptions.EncryptException;
	import upl.util.LinkedHashMap;
	import upl.util.Map;
	
	public class Prefs extends Preferences {
		
		private SharedPreferences prefs;
		private SharedPreferences.Editor editor;
		
		public Prefs (Context context, String name) {
			this (context, name, Context.MODE_PRIVATE);
		}
		
		public Prefs (Context context, String name, int mode) {
			
			super ();
			
			prefs = context.getSharedPreferences (name, mode);
			editor = edit ();
			
		}
		
		public Map<String, Typeface> getTypefaces () {
			
			Map<String, Typeface> typefaces = new LinkedHashMap<> ();
			
			typefaces.add ("serif", Typeface.SERIF);
			typefaces.add ("sans_serif", Typeface.SANS_SERIF);
			typefaces.add ("monospace", Typeface.MONOSPACE);
			typefaces.add ("default", Typeface.DEFAULT);
			typefaces.add ("default_bold", Typeface.DEFAULT_BOLD);
			
			return typefaces;
			
		}
		
		public Typeface getFontFamily (String key, Typeface defVal) {
			
			Map<String, Typeface> typefaces = getTypefaces ();
			
			String value = typefaces.getKey ((Typeface) defValue (key, defVal));
			return typefaces.get (getString (key, value));
			
		}
		
		@Override
		public String getString (String key, String value, boolean encrypt) throws DecryptException {
			
			if (encrypt) value = ru.ointeractive.andromeda.Crypto.decrypt (value);
			return prefs.getString (key, value);
			
		}
		
		@Override
		public int getInt (String key, int value) {
			return prefs.getInt (key, value);
		}
		
		@Override
		public long getLong (String key, long value) {
			return prefs.getLong (key, value);
		}
		
		@Override
		public float getFloat (String key, float value) {
			return prefs.getFloat (key, value);
		}
		
		@Override
		public boolean getBool (String key, boolean value) {
			return prefs.getBoolean (key, value);
		}
		
		public int getColor (String key) {
			return getColor (key, defValue (key, null).toString ());
		}
		
		public int getColor (String key, String defColor) {
			return Color.valueOf (get (key, defColor));
		}
		
		public SharedPreferences.Editor edit () {
			return prefs.edit ();
		}
		
		@Override
		public void put (String key, Object value, boolean encrypt) throws EncryptException {
			
			if (value instanceof Integer)
				editor.putInt (key, (int) value);
			else if (value instanceof Long)
				editor.putLong (key, (long) value);
			else if (value instanceof Float)
				editor.putFloat (key, (float) value);
			else if (value instanceof Double)
				editor.putLong (key, Double.doubleToRawLongBits ((double) value));
			else if (value instanceof Boolean)
				editor.putBoolean (key, (boolean) value);
			else {
				
				String str = String.valueOf (value);
				
				if (encrypt) str = ru.ointeractive.andromeda.Crypto.encrypt (str);
				editor.putString (key, str);
				
			}
			
		}
		
		@Override
		public void apply () {
			
			if (OS.SDK >= 9)
				editor.apply ();
			else
				editor.commit (); // SUPPORT 9
			
		}
		
		public void setColor (String key, int color) {
			set (key, Color.toString (color));
		}
		
		public void setFontFamily (String key, String value) {
			set (key, value);
		}
		
		@Override
		public boolean contains (String string) {
			return prefs.contains (string);
		}
		
	}