package com.github.tbwork.anole.loader.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


public class ProjectUtil {
	
	public static List<String> localResourceProtocals = SetUtil.newArrayList("file:", "jar:", "mailTo:");

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
	 * The classpath of the application which may have the following cases:<br>
	 * For <b>normal java-file application within IDE</b>, it returns: "/.../classes/".<br>
	 * For <b>JUnit java-file application within IDE</b>, it returns: "/.../test-classes/".<br>
	 * For <b>normal jar-file application</b>, it returns: "/.../xxx.jar!/".<br>
	 * For <b>spring jar-file application</b>, it returns: "/.../xxx.jar!/BOOT-INF!/classes!/".<br>
	 */
	public static String getAppClasspath() {
		String classpathsString = System.getProperty("java.class.path");
		if(classpathsString == null){
			classpathsString = "";
		}
		String [] classpaths = classpathsString.split(System.getProperty("path.separator"));
		String resultClasspath = null;
		for(String classpath : classpaths){
			classpath = PathUtil.format2Slash(classpath);
			if(classpath.contains("/target/classes")){
				resultClasspath = classpath;
				break;
			}
			else if(classpath.contains("/target/test-classes")){
				resultClasspath = classpath;
				break;
			}
		}

		if(resultClasspath == null) {
			for(String classpath : classpaths){
				if(classpath.endsWith(".jar")){
					if(classpath.contains("/")){
						resultClasspath = classpath;
						break;
					}
					else{
						resultClasspath = System.getProperty("user.dir")+"/"+classpath;
						break;
					}
				}
			}
		}

		resultClasspath = PathUtil.getNakedAbsolutePath(resultClasspath);
		if(AnoleFileUtil.isSpringFatJar(resultClasspath)){
			return resultClasspath+"!/BOOT-INF!/classes!/";
		}
		resultClasspath =  resultClasspath.endsWith("/") ? resultClasspath : resultClasspath+"/";

		return resultClasspath.startsWith("/") ? resultClasspath : "/" + resultClasspath;

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
