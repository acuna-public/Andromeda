	package ru.ointeractive.andromeda;
	
	import android.app.PendingIntent;
	import android.content.Context;
	import android.content.Intent;
	
	import ru.ointeractive.andromeda.apps.AppsService;
	import upl.core.Calendar;
	import upl.core.Log;
	
	public class AlarmManager {
	  
	  protected final Context context;
		
		private PendingIntent pIntent;
	  
	  public android.app.AlarmManager alarmManager;
		
		protected Integer type;
	  
	  public static long INTERVAL_WEEK = (android.app.AlarmManager.INTERVAL_DAY * 7);
		public static long INTERVAL_MONTH = (INTERVAL_WEEK * 4);
		
		public AlarmManager (Context context) {
			this.context = context;
		}
		
		public AlarmManager setType (Integer type) {
	    
	    this.type = type;
	    return this;
	    
	  }
		
		public AlarmManager setIntent (Intent intent, int requestCode) {
	    return setIntent (PendingIntent.getBroadcast (context, requestCode, intent, AppsService.flagUpdateCurrent ()));
	  }
	  
	  public AlarmManager setIntent (PendingIntent intent) {
	    
	    pIntent = intent;
		  alarmManager = (android.app.AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
		  
		  return this;
	   
	  }
		
		public AlarmManager start (long startTime) {
	   
		  if (type == null) setType (android.app.AlarmManager.RTC_WAKEUP);
			
		  if (OS.SDK >= 23)
			  alarmManager.setExactAndAllowWhileIdle (type, startTime, pIntent);
			else if (OS.SDK >= 19)
				alarmManager.setExact (type, startTime, pIntent);
			else
			  alarmManager.set (type, startTime, pIntent);
			
	    return this;
	    
	  }
	  
	  public AlarmManager stop () {
			
		  alarmManager.cancel (pIntent);
		  pIntent.cancel ();
		  
	    return this;
	    
	  }
		
	}