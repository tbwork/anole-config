package org.tbwork.anole.hub.server.repository;

public class DataService {

	public static String getProperty(String key){
		
		if("1".equals(key)){
			return "1-value";
		}
		else if("2".equals(key)){
			return "2-value";
		}
		else if("3".equals(key)){
			return "3-value";
		}
		else if("4".equals(key)){
			return "4-value";
		}
		else if("5".equals(key)){
			return "5-value";
		}
		else
		{
			return "default-value";
		}
	}
	
	
}
