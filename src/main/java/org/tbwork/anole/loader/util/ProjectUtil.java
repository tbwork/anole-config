package org.tbwork.anole.loader.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.common.collect.Lists;

public class ProjectUtil {
	
	public static List<String> localResourceProtocals = Lists.newArrayList("file:", "jar:", "mailTo:");

	public static String getJarPath(String fullPath){
		int index = fullPath.indexOf("!/");
		if(index < 0){
			throw new RuntimeException("The input path is not a jar resource! input path is : "+ fullPath);
		}
		return fullPath.substring(0, index);
	}
	
	public static String getUrlLocalPath(URL url) throws MalformedURLException{
		String urlString = url.toString();
		for(String protocal : localResourceProtocals){
			urlString = urlString.replaceFirst(protocal, "");
		}
		return urlString;
	}
	 
}
