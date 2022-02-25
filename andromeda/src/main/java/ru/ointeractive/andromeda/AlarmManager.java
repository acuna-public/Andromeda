	package ru.ointeractive.andromeda;
	
	import android.app.PendingIntent;
	import android.content.Context;
	import android.content.Intent;
	
	import upl.core.Calendar;
	
	public class AlarmManager {
	  
	  private final Context context;
	  
	  private PendingIntent pIntent;
	  
	  private android.app.AlarmManager alarmManager;
	  
	  public Integer type;
	  
	  public static long INTERVAL_WEEK = (android.app.AlarmManager.INTERVAL_DAY * 7);
		public static long INTERVAL_MONTH = (INTERVAL_WEEK * 4);
		
		public AlarmManager (Context context) {
	    this.context = context;
	  }
	  
	  public AlarmManager setType (Integer type) {
	    
	    this.type = type;
	    return this;
	    
	  }
		
		public AlarmManager setIntent (Class<?> service) {
			return setIntent (service, 0);
		}
		
		public AlarmManager setIntent (Class<?> service, int requestCode) {
	    return setIntent (new Intent (context, service), requestCode);
	  }
		
		public AlarmManager setIntent (Intent intent) {
			return setIntent (intent, 0);
		}
		
		public AlarmManager setIntent (Intent intent, int requestCode) {
	    return setIntent (PendingIntent.getBroadcast (context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT));
	  }
	  
	  public AlarmManager setIntent (PendingIntent intent) {
	    
	    pIntent = intent;
		  alarmManager = (android.app.AlarmManager) context.getSystemService (Context.ALARM_SERVICE);
		  
		  return this;
	   
	  }
		
		public AlarmManager start (Calendar calendar, long interval) {
			return start (calendar.getTimeInMillis (), interval);
		}
		
		public AlarmManager start (long startTime, long interval) {
	  	
		  if (type == null) setType (android.app.AlarmManager.RTC);
			
		  if (interval >= 0)
			  alarmManager.setRepeating (type, startTime, interval, pIntent);
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