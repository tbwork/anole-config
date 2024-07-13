package com.github.tbwork.anole.loader.util;

import com.github.tbwork.anole.loader.enums.OsCategory;

public class OsUtil {

	public static OsCategory getOsCategory(){
		String os = System.getProperty("os.name");  
		if(os.toLowerCase().startsWith("win")){  
		    return OsCategory.WINDOWS;
		}  
		else if(os.toLowerCase().startsWith("mac")){
			return OsCategory.MAC;
		}
		else{ // linux
			return OsCategory.LINUX;
		} 
	}
}
