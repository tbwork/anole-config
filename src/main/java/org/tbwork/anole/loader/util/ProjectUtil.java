package org.tbwork.anole.loader.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.tbwork.anole.loader.context.Anole;
 

public class ProjectUtil {
	
	public static List<String> localResourceProtocals = SetUtil.newArrayList("file:", "jar:", "mailTo:");

	private static String userClassPath;
	
	private static String applicationClassPath;
	
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
	
	public static String getUserClasspath() {
		if(userClassPath != null && !userClassPath.isEmpty()) {
			return userClassPath;
		}
		Class<?> mainClass = Anole.getMainClass();
		if(mainClass == null)
			mainClass = getRootClassByStackTrace(); 
		URL userClassLoadPathUrl = mainClass.getResource(""); 
		String classRelativePath = mainClass.getPackage().getName().replace(".", "/")+"/";
		userClassPath = FileUtil.getRealAbsolutePath(userClassLoadPathUrl.toString().replace(classRelativePath, ""));  
		userClassPath = FileUtil.format2Slash(userClassPath);  
		return userClassPath;
	} 

	
	public static String getApplicationClasspath() {
		if(applicationClassPath!=null && !applicationClassPath.isEmpty())
			return applicationClassPath;
		
		URL applicationClassLoadPathUrl = Thread.currentThread().getContextClassLoader().getResource("");
		applicationClassPath = FileUtil.getRealAbsolutePath(applicationClassLoadPathUrl.toString());  
		applicationClassPath = FileUtil.format2Slash(applicationClassPath);  
		return applicationClassPath;
	} 
	
	public static Class<?> getRootClassByStackTrace(){
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace(); 
			if(stackTrace.length > 0)
				return Class.forName(stackTrace[stackTrace.length-1].getClassName());
			throw new ClassNotFoundException("Could not find the root class of current thread");
		}
		catch (ClassNotFoundException ex) {
			// Swallow and continue
			return null;
		} 
	} 
}
