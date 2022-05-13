	package ru.ointeractive.andromeda.plugins.device.desktop.windows;
	
	import upl.lang.Application;
	
	public class DevicePlugin extends ru.ointeractive.andromeda.plugins.device.DevicePlugin {
		
		public DevicePlugin (Application app) {
			super (app);
		}
		
		@Override
		protected String getUUIDCommand () {
			return "wmic baseboard get serialnumber";
		}
		
	}