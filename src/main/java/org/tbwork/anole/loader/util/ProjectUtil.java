package org.tbwork.anole.loader.util;

public class ProjectUtil {
  
	public static String getClassPath(){  
		return	Thread.currentThread().getContextClassLoader().getResource("/").getPath() ;
	}
	 
}
