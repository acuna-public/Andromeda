	package ru.ointeractive.andromeda.plugins.device.desktop.dalvik;
	
	import upl.lang.Application;
	
	public class DevicePlugin extends ru.ointeractive.andromeda.plugins.device.DevicePlugin {
		
		public DevicePlugin (Application app) {
			super (app);
		}
		
		@Override
		protected String getUUIDCommand () {
			return "sudo dmidecode -s baseboard-serial-number";
		}
		
	}