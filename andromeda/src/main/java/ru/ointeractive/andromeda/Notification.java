	package ru.ointeractive.andromeda;
	
	import android.app.NotificationChannel;
	import android.app.NotificationManager;
	import android.app.PendingIntent;
	import android.content.Context;
	import android.content.Intent;
	import android.graphics.Color;
	import android.os.Build;
	
	import java.lang.reflect.Method;
	
	import ru.ointeractive.andromeda.graphic.Graphic;
	
	public class Notification extends android.app.Notification {
		
		private final Context context;
		
		private Integer smallIcon, largeIcon;
		private int mPriority = 0;
		private String title, text, mFullText = "";
		private boolean mProgress = false, indeterminate = false, cancelable = true;
		private PendingIntent pendingIntent;
		private android.app.Notification notification15;
		private Builder builder;
		
		public Notification (Context context) {
			
			this.context = context;
			
			manager = (NotificationManager) context.getSystemService (Context.NOTIFICATION_SERVICE);
			
		}
		
		public Notification setIntent (Intent intent) {
			
			setIntent (PendingIntent.getActivity (context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT));
			return this;
			
		}
		
		public Notification setIntent (PendingIntent intent) {
			
			this.pendingIntent = intent;
			return this;
			
		}
		
		public Notification setTitle (int title) {
			return setTitle (context.getString (title));
		}
		
		public Notification setTitle (String title) {
			
			this.title = title;
			return this;
			
		}
		
		public Notification setText (int text) {
			return setText (context.getString (text));
		}
		
		public Notification setText (String text) {
			
			this.text = text;
			return this;
			
		}
		
		public Notification setFullText (int text) {
			return setFullText (context.getString (text));
		}
		
		public Notification setFullText (String text) {
			
			mFullText = text;
			return this;
			
		}
		
		public Notification setSmallIcon (int icon) {
			
			smallIcon = icon;
			return this;
			
		}
		
		public Notification setIcon (int icon) {
			
			largeIcon = icon;
			return this;
			
		}
		
		public Notification isCancelable (boolean value) {
			
			cancelable = value;
			return this;
			
		}
		
		public Notification setIndeterminate (boolean value) {
			
			this.indeterminate = value;
			return this;
			
		}
		
		public Notification setProgress (boolean value) {
			
			mProgress = value;
			return this;
			
		}
		
		public Notification setPriority (int value) {
			
			mPriority = value;
			return this;
			
		}
		
		protected void _setPriority (int value) {
			
			if (OS.SDK >= Build.VERSION_CODES.JELLY_BEAN) {
				
				switch (value) {
					
					default:
						builder.setPriority (PRIORITY_MAX);
						break;
					
					case 1:
						builder.setPriority (PRIORITY_HIGH);
						break;
					
					case 2:
						builder.setPriority (PRIORITY_DEFAULT);
						break;
					
					case 3:
						builder.setPriority (PRIORITY_LOW);
						break;
					
					case 4:
						builder.setPriority (PRIORITY_MIN);
						break;
					
				}
				
			}
		
		}
		
		public Notification show () {
			return show (0);
		}
		
		public NotificationManager manager;
		
		public Notification show (int id) {
			
			boolean hasIcon = (smallIcon != null);
			
			if (!hasIcon) setSmallIcon (largeIcon);
			
			if (OS.SDK >= 16) {
				
				builder = new Builder (context)
					          .setContentTitle (title)
					          .setContentText (text)
					          .setSmallIcon (smallIcon);
				
				if (hasIcon && largeIcon != null)
					builder.setLargeIcon (Graphic.toBitmap (context, largeIcon));
				
				if (mProgress)
					builder.setProgress (100, 0, indeterminate);
				
				if (!mFullText.equals (""))
					builder.setStyle (new BigTextStyle (builder).bigText (mFullText));
				
				_setPriority (mPriority);
				
			} else
				notification15 = new android.app.Notification (smallIcon, text, java.lang.System.currentTimeMillis ());
			
			if (OS.SDK < 16) { // SUPPORT 16
				
				try {
					
					Method method = notification15.getClass ().getMethod ("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
					method.invoke (notification15, context, title, text, pendingIntent);
					
				} catch (Exception e) {
					// empty
				}
				
			} else builder.setContentIntent (pendingIntent);
			
			if (OS.SDK < 16) {
				
				if (cancelable) notification15.flags |= FLAG_AUTO_CANCEL;
				
			} else builder.setOngoing (!cancelable);
			
			if (manager != null) {
				
				if (OS.SDK >= 16) {
					
					if (OS.SDK >= 26) {
						
						manager.createNotificationChannel (new NotificationChannel ("notify", title, NotificationManager.IMPORTANCE_DEFAULT));
						builder.setChannelId ("notify");
						
					}
					
					manager.notify (id, builder.build ());
					
				} else manager.notify (id, notification15);
				
			}
			
			return this;
			
		}
		
		public Notification createChannel (String id, String name, String descr) {
			
			if (OS.SDK >= Build.VERSION_CODES.O) {
				
				NotificationChannel channel = new NotificationChannel (id, name, NotificationManager.IMPORTANCE_HIGH);
				
				channel.setDescription (descr);
				channel.enableLights (true);
				channel.setLightColor (Color.RED);
				channel.enableVibration (false);
				
				manager.createNotificationChannel (channel);
				
			}
			
			return this;
			
		}
		
		public Notification deleteChannel (String id) {
			
			if (OS.SDK >= Build.VERSION_CODES.O)
				manager.deleteNotificationChannel (id);
			
			return this;
			
		}
		
	}