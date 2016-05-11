package org.tbwork.anole.hub.util;

public class ProjectUtil {

	public static String getProjectName(String configKeyName){ 
		return configKeyName.substring(0, configKeyName.indexOf('.')); 
	}
	 
	
}
