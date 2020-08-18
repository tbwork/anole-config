package org.tbwork.anole.loader.util;

import org.tbwork.anole.loader.AnoleApp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


public class ProjectUtil {
	
	public static List<String> localResourceProtocals = SetUtil.newArrayList("file:", "jar:", "mailTo:");

	private static String callerClasspath; 
	private static String programPath;
	
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
	 * The classpath of the class which called Anole boot classes directly.<br>
	 * For <b>normal java-file application within IDE</b>, it returns: "/.../classes/".<br>
	 * For <b>JUnit java-file application within IDE</b>, it returns: "/.../test-classes/".<br>
	 * For <b>other java-file application</b>, it returns the root directory of the program.<br>
	 * For <b>normal jar-file application</b>, it returns: "/.../xxx.jar!/".<br>
	 * For <b>spring jar-file application</b>, it returns: "/.../xxx.jar!/BOOT-INF/classes!/".<br>
	 */
	public static String getCallerClasspath() {
		if(callerClasspath != null && !callerClasspath.isEmpty()) {
			return callerClasspath;
		}
		Class<?> mainClass = AnoleApp.getCallerClass();
		String fullClassName = mainClass.getName(); 
		String packageName = mainClass.getPackage().getName();
		String className = fullClassName.replace(packageName+".", "");
		String classRelativePath = mainClass.getPackage().getName().replace(".", "/")+"/"+className+".class";
		URL resourcePath = Thread.currentThread().getContextClassLoader().getResource(classRelativePath); 
		callerClasspath = PathUtil.getNakedAbsolutePath(resourcePath.toString().replace(classRelativePath, ""));
		callerClasspath = PathUtil.format2Slash(callerClasspath);
		return callerClasspath;
	}
	
	/**
	 * The path where the program in running under.<br>
	 * Usually, it is the path where the "java -jar" is typing in.<br>
	 */
	public static String getProgramPath() {
		if(programPath != null && !programPath.isEmpty())
			return programPath;
		programPath = System.getProperty("user.dir"); 
		programPath = PathUtil.format2Slash(programPath);
		if(!programPath.endsWith("/"))
			programPath = programPath + "/";
		return programPath;
	}

	/**
	 * Get potential class loaders.
	 *
	 * @return
	 */
	public static ClassLoader[] getClassLoaders() {
		final Collection<ClassLoader> classLoaders = new LinkedHashSet<>();
		classLoaders.add(ProjectUtil.class.getClassLoader());
		final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (systemClassLoader != null) {
			classLoaders.add(systemClassLoader);
		}
		return classLoaders.toArray(new ClassLoader[classLoaders.size()]);
	}
 
  
}
