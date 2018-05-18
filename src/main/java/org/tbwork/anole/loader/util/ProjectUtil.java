package org.tbwork.anole.loader.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.enums.OsCategory; 
 

public class ProjectUtil {
	
	public static List<String> localResourceProtocals = SetUtil.newArrayList("file:", "jar:", "mailTo:");

	private static String mainclassClasspath;
	
	private static String applicationClasspath;
	
	private static String programClasspath; 
	
	private static String homeClasspath;
	
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
	 * The classpath where the root main class is running under.<br>
	 * For <b>normal java-file application within IDE</b>, it returns: "/.../classes/".<br>
	 * For <b>JUnit java-file application within IDE</b>, it returns the IDE JUnit test's running path like "D:/IDE/oxygen/configuration/org.eclipse.osgi/412/0/.cp/".<br>
	 * For <b>other java-file application</b>, it returns the root directory of the program.<br>
	 * For <b>normal jar-file application</b>, it returns: "/.../xxx.jar!/".<br>
	 * For <b>spring jar-file application</b>, it returns: "/.../xxx.jar!/".<br>
	 */
	public static String getRootMainclassClasspath() {
		if(mainclassClasspath != null && !mainclassClasspath.isEmpty()) {
			return mainclassClasspath;
		}
		Class<?> mainClass = Anole.getMainClass();
		if(mainClass == null)
			mainClass = getRootClassByStackTrace();  
		System.out.println("用户主类："+mainClass.getName());
		String fullClassName = mainClass.getName();
		String packageName = mainClass.getPackage().getName();
		String className = fullClassName.replace(packageName+".", "");
		String classRelativePath = mainClass.getPackage().getName().replace(".", "/")+"/"+className+".class";
		URL resourcePath = Thread.currentThread().getContextClassLoader().getResource(classRelativePath); 
		mainclassClasspath = FileUtil.getNakedAbsolutePath(resourcePath.toString().replace(classRelativePath, ""));  
		mainclassClasspath = FileUtil.format2Slash(mainclassClasspath);  
		return mainclassClasspath;
	}
	
	 

	/**
	 * Return the classpath where the user's main class is running under.
	 * For <b>java-file application within IDE</b>, it returns: ".../classes/".<br>
	 * For <b>other java-file application</b>, it returns the root directory of the program.<br>
	 * For <b>normal jar-file application</b>, it returns the parent directory of the jar.<br>
	 * For <b>spring jar-file application</b>, it returns: ".../xxx.jar!/BOOT-INF/classes/".<br>
	 */
	public static String getApplicationClasspath() {
		if(applicationClasspath!=null && !applicationClasspath.isEmpty())
			return applicationClasspath;
		URL applicationClassLoadPathUrl = Thread.currentThread().getContextClassLoader().getResource("");
		applicationClasspath = FileUtil.getNakedAbsolutePath(applicationClassLoadPathUrl.toString());  
		applicationClasspath = FileUtil.format2Slash(applicationClasspath);  
		return applicationClasspath;
	} 
	
	/**
	 * The classpath where the main class is running under.<br>
	 * For <b>java-file application within IDE</b>, it returns: ".../classes/" or ".../test-classes/".<br>
	 * For <b>other java-file application</b>, it returns the root directory of the program.<br>
	 * For <b>normal jar-file application</b>, it returns: ".../xxx.jar!/".<br>
	 * For <b>spring jar-file application</b>, it returns: ".../xxx.jar!/".<br>
	 */
	public static String getHomeClasspath() {
		if(homeClasspath != null)
			return homeClasspath;
		String mainclassClasspath = getMainclassClasspath();
		String applicationClasspath = getApplicationClasspath();
		if(mainclassClasspath.startsWith(applicationClasspath) || applicationClasspath.startsWith(mainclassClasspath)) {
			// corresponding with application classpath
			homeClasspath = mainclassClasspath ;
		}
		else {
			homeClasspath = applicationClasspath;
		} 
		return homeClasspath;
	}
	
	
	
	/**
	 * The classpath where the program is under.
	 * For <b>java-file application within IDE</b>, it returns: ".../classes/".<br>
	 * For <b>other java-file application</b>, it returns the root directory of the program.<br>
	 * For <b>normal jar-file application</b>, it returns the parent directory of the jar.<br>
	 * For <b>spring jar-file application</b>, it returns the parent directory of the jar.<br>
	 */
	public static String getProgramClasspath() {
		if(programClasspath!=null && !programClasspath.isEmpty())
			return programClasspath;
		String homeClasspath = getHomeClasspath();
		if(homeClasspath.endsWith("/"))
			homeClasspath = homeClasspath.substring(0, homeClasspath.length() -1);
		String [] pathParts = homeClasspath.split("/");
		if(pathParts[pathParts.length-1].endsWith(".jar!")) {  
			String result = StringUtil.join("/", SetUtil.copyArray(pathParts, 0, pathParts.length-1));
			programClasspath = result + "/"; 
		}
		else {
			programClasspath = getHomeClasspath();
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
