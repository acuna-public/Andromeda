	package ru.ointeractive.andromeda;
	
	import android.app.Activity;
	import android.os.Bundle;
	
	import java.util.HashMap;
	import java.util.Map;
	
	public class Intent extends android.content.Intent {
		
		public Intent (Activity activity, Class<?> clazz) {
			super (activity, clazz);
		}
		
		public Map<String, String> toMap () {
			
			Map<String, String> output = new HashMap<> ();
			Bundle bundle = getExtras ();
			
			for (String key : bundle.keySet ())
				output.put (key, getStringExtra (key));
			
			return output;
			
		}
		
	}