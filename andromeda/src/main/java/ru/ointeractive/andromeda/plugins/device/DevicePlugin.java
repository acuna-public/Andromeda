	package ru.ointeractive.andromeda.plugins.device;
	
	import upl.lang.Application;
	import upl.core.Console;
	import upl.lang.Plugin;
	import upl.core.exceptions.ConsoleException;
	import upl.core.exceptions.PluginException;
	
	public class DevicePlugin extends Plugin<DevicePlugin> {
		
		public DevicePlugin (Application app) {
			super (app);
		}
		
		protected String getUUIDCommand () {
			return null;
		}
		
		public String getUUID () throws PluginException {
			
			try {
				return new Console ().query (getUUIDCommand ()).get (0);
			} catch (ConsoleException e) {
				throw new PluginException (e);
			}
			
		}
		
	}