package org.tbwork.anole.loader.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.tbwork.anole.loader.context.Anole; 
 

public class ProjectUtil {
	
	public static List<String> localResourceProtocals = SetUtil.newArrayList("file:", "jar:", "mailTo:");

	private static String mainclassClasspath;
	
	private static String applicationClasspath;
	
	private static String programClasspath;
	
	private static String currentJarName;
	
	
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
	
	/**
	 * The classpath where is the main class under.
	 * @return
	 */
	public static String getMainclassClasspath() {
		if(mainclassClasspath != null && !mainclassClasspath.isEmpty()) {
			return mainclassClasspath;
		}
		Class<?> mainClass = Anole.getMainClass();
		if(mainClass == null)
			mainClass = getRootClassByStackTrace(); 
		URL userClassLoadPathUrl = mainClass.getResource(""); 
		String classRelativePath = mainClass.getPackage().getName().replace(".", "/")+"/";
		mainclassClasspath = FileUtil.getRealAbsolutePath(userClassLoadPathUrl.toString().replace(classRelativePath, ""));  
		mainclassClasspath = FileUtil.format2Slash(mainclassClasspath);  
		return mainclassClasspath;
	} 

	
	public static String getApplicationClasspath() {
		if(applicationClasspath!=null && !applicationClasspath.isEmpty())
			return applicationClasspath;
		URL applicationClassLoadPathUrl = Thread.currentThread().getContextClassLoader().getResource("");
		applicationClasspath = FileUtil.getRealAbsolutePath(applicationClassLoadPathUrl.toString());  
		applicationClasspath = FileUtil.format2Slash(applicationClasspath);  
		return applicationClasspath;
	} 
	
	public static String getProgramClasspath() {
		if(programClasspath!=null && !programClasspath.isEmpty())
			return programClasspath;
		String mainclassClasspath = getMainclassClasspath();
		if(mainclassClasspath.endsWith("/"))
			mainclassClasspath = mainclassClasspath.substring(0, mainclassClasspath.length() -1);
		String [] pathParts = mainclassClasspath.split("/");
		if(pathParts[pathParts.length-1].endsWith(".jar!")) {  
			String result = StringUtil.join("/", SetUtil.copyArray(pathParts, 0, pathParts.length-1));
			programClasspath = result + "/"; 
		}
		else {
			programClasspath = getMainclassClasspath();
		} 
		return programClasspath; 
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
