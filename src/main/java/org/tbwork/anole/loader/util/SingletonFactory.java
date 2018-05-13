package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.core.manager.impl.LocalConfigManager;

public class SingletonFactory { 
	
	private static final LocalConfigManager localConfigManager  = new LocalConfigManager();
	 
	public static LocalConfigManager getLocalConfigManager(){
		return localConfigManager;
	}
	
}
