package org.tbwork.anole.loader.core.loader.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.BadJarFileException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.AnoleLogger.LogLevel;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.SingletonFactory;
import org.tbwork.anole.loader.util.StringUtil; 

public class AnoleFileLoader implements AnoleLoader{ 
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private ConfigManager cm;
	
	private static final List<String> projectInfoPropertiesFileList  = new ArrayList<String>();
	
	static {
		projectInfoPropertiesFileList.add("../maven-archiver/pom.properties");
		projectInfoPropertiesFileList.add("META-INF/maven/*/*/pom.properties");
	}
	
	public AnoleFileLoader(){
		cm = SingletonFactory.getLocalConfigManager();
	}
	
	public AnoleFileLoader(ConfigManager cm){
		this.cm = cm ;
	}
	

	@Override
	public Map<String,FileLoadStatus> load() {
		return load(AnoleLogger.defaultLogLevel);
	}  
 
	@Override
	public Map<String,FileLoadStatus> load(LogLevel logLevel) { 
		AnoleLogger.anoleLogLevel = logLevel; 
	    throw new OperationNotSupportedException();
	}

	@Override
	public Map<String,FileLoadStatus> load(String... configLocations) {
		return load(AnoleLogger.defaultLogLevel, configLocations);
	} 
	
	@Override
	public Map<String,FileLoadStatus> load(LogLevel logLevel, String... configLocations) { 
		Map<String,FileLoadStatus> result = new HashMap<String, FileLoadStatus>();
		AnoleLogger.anoleLogLevel = logLevel; 
		LogoUtil.decompress("===",  "https://github.com/tbwork/anole-loader", "Version: 1.2.4");
		AnoleLogger.debug("Current enviroment is {}", Anole.getEnvironment());
		Anole.setMainClass(getRootClassByStackTrace());
		AnoleLogger.debug("Searching configuration files which match:");   
		for(String configLocation : configLocations) {
			AnoleLogger.debug(configLocation);
			FileLoadStatus fls = loadFile(configLocation.trim());
			result.put(configLocation,  fls); 
		} 
		AnoleLogger.debug("Searching project information files:");   
		for(String projectInfoFile : getFullPathForProjectInfoFiles()) {
			AnoleLogger.debug(projectInfoFile);
			loadFile(projectInfoFile.trim(), true);
		}
		Anole.initialized = true; 
		cm.postProcess();
		AnoleLogger.info("[:)] Anole configurations are loaded succesfully.");
		return result;
	}
	
	private static Class<?> getRootClassByStackTrace(){
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
	
	private List<String> getFullPathForProjectInfoFiles() {
		List<String> result = new ArrayList<String>();
		String userClasspath =  ProjectUtil.getMainclassClasspath();
		for(String projectInfoFile : projectInfoPropertiesFileList) {
			result.add(userClasspath + projectInfoFile); 
		}
		return result;
	}
	  
	private InputStream newInputStream(String filepath){ 
		File file = new File(filepath);
		if(file.exists()){
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// never goes here
			}
		}
	    return null;
	}
	 
	/**
	 * @param fileFullPath the absolute path of the configuration file
	 */
	protected FileLoadStatus loadFile(String fileFullPath, boolean ignoreJarInJar){
		AnoleLogger.debug("Loading config files matchs '{}'", fileFullPath);
		if(fileFullPath.contains("!/")){ // For Jar projects
			Anole.setRuningInJar(true);
			return loadFileFromJar(fileFullPath, ignoreJarInJar);
		}
		else{
			Anole.setRuningInJar(false);
			return loadFileFromDirectory(fileFullPath);
		}
	} 

	protected FileLoadStatus loadFile(String fileFullPath){
		return loadFile(fileFullPath, false);
	}

	
	private FileLoadStatus loadFileFromDirectory(String fileFullPath){ 
		if(!fileFullPath.contains("*")){
			InputStream is = newInputStream(fileFullPath);
			if(is == null)
				return FileLoadStatus.NOT_FOUND;
			acfParser.parse(is, fileFullPath);
			return FileLoadStatus.SUCCESS;
		}
		else
		{  
			if(FileUtil.isFuzzyDirectory(fileFullPath)){
				AnoleLogger.warn("Use asterisk in directory is not recomended, e.g., D://a/*/*.txt. We hope you know that it will cause plenty cost of time to seek every matched file.");
			}
			String solidDirectory = FileUtil.getSolidDirectory(fileFullPath);
			File directory = new File(solidDirectory);
			if(!directory.exists()) {
				return FileLoadStatus.NOT_MATCHED;
			} 
			List<File> files = FileUtil.getFilesInDirectory(solidDirectory);
			boolean matched = false;
			for(File file : files){
				if(FileUtil.asteriskMatchPath(fileFullPath,  uniformAbsolutePath(file.getAbsolutePath()))){
					 acfParser.parse(newInputStream(file.getAbsolutePath()), file.getAbsolutePath());
					 matched = true;
				}
			}
			return matched ? FileLoadStatus.SUCCESS : FileLoadStatus.NOT_MATCHED;
		}
	}
	
	private String uniformAbsolutePath(String absolutePath) {
		return FileUtil.getRealAbsolutePath(FileUtil.format2Slash(absolutePath));
	}
	
	 
	/**
	 * Input like : /D://prject/a.jar!/BOOT-INF!/classes!/*.properties
	 * @param fileFullPath the full path of the configuration file.
	 * @param ignoreJarInJar whether ignore the jars inner the main jar.
	 * @return
	 */
	private FileLoadStatus loadFileFromJar(String fileFullPath, boolean ignoreJarInJar){
		String jarPath = ProjectUtil.getJarPath(fileFullPath)+"/"; 
	    String directRelativePath = fileFullPath.replace("!", "").replace(jarPath, "");
	    JarFile file;
		try {
			file = new JarFile(jarPath);
		}
		catch(FileNotFoundException e) {
			if(fileFullPath.contains("*")) {
				//asterisk match
				return FileLoadStatus.NOT_MATCHED;
			}
			else {
				return FileLoadStatus.NOT_FOUND;
			} 
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new BadJarFileException(jarPath);
		}
		Enumeration<JarEntry> entrys = file.entries();
		boolean matched = false;
		while(entrys.hasMoreElements()){
	        JarEntry fileInJar = entrys.nextElement();
	        String fileInJarName = fileInJar.getName();
	        if(fileInJarName.endsWith(".jar") && !ignoreJarInJar) {
	        	//another jar 
	        	List<InputStream> inputStreams = getConfigInputStreamsFromJar(getInputStream(file, fileInJar), FileUtil.format2Slash(directRelativePath));
	        	for(InputStream is : inputStreams) {
	        		matched = true;
	        		acfParser.parse(is, fileInJarName);
	        	}
	        	continue;
	        }
	        if(FileUtil.asteriskMatchPath(FileUtil.format2Slash(directRelativePath), FileUtil.format2Slash(fileInJarName))){
	        	AnoleLogger.debug("New config file ({}) was found. Parsing..." + fileInJarName); 
	        	matched = true;
				acfParser.parse(getInputStream(file, fileInJar), fileInJarName);
			}
		}    
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if(matched) {
			return  FileLoadStatus.SUCCESS;
		}
		else {
			return fileFullPath.contains("*") ? FileLoadStatus.NOT_MATCHED : FileLoadStatus.NOT_FOUND;
		}  
	}
	
	private static List<InputStream> getConfigInputStreamsFromJar(InputStream jarFileInputStream, String directRelativePath){ 
		List<InputStream> result = new ArrayList<InputStream>();  
		try {
			ZipInputStream jarInputStream = new ZipInputStream(jarFileInputStream); 
			ZipEntry zipEntry = null; 
			while ((zipEntry = jarInputStream.getNextEntry()) != null) { 
				String fileInZipName = zipEntry.getName();
				if(FileUtil.asteriskMatchPath(directRelativePath, FileUtil.format2Slash(fileInZipName))){
	    			System.out.println("New config file ({}) was found. Parsing..." + fileInZipName); 
	    			result.add(getZipInputStream(jarInputStream, zipEntry));
	    		} 
			} 
		}
		catch(Exception e) { 
			AnoleLogger.error("Fail to get configuration file from jar due to {}", e.getMessage());
		} 
		return result;
	}
	
	
	private static InputStream getZipInputStream(InputStream in, ZipEntry entry)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		long size = entry.getSize();
		if (size > -1) {
			byte[] buffer = new byte[1024 * 4];
			int n = 0;
			long count = 0;
			while (-1 != (n = in.read(buffer)) && count < size) {
				baos.write(buffer, 0, n);
				count += n;
			}
		} else {
			while (true) {
				int b = in.read();
				if (b == -1) {
					break;
				}
				baos.write(b);
			}
		} 
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	
	private InputStream getInputStream(JarFile jf, JarEntry je) {
		try {
			return jf.getInputStream(je);
		}
		catch(Exception e) {
			// never goes here if the previous file is ok 
			return null;
		}
	}
	
	private static class  LogoUtil{
		
		 private static void print(String str) {
			 System.out.print(str);
		 }
		 private static void print(char khar) {
			 System.out.print(khar);
		 }
		 private static void println(String str) {
			 System.out.println(str);
		 }
		 private static void println(char khar) {
			 System.out.println(khar);
		 }
		 
		 public static void decompress(String line1, String line2, String line3){
			    InputStream in = AnoleFileLoader.class.getResourceAsStream("/logo.cps");  
		    	Scanner scanner = null;
		    	List<Integer> chars = new ArrayList<Integer>();
				try {
					scanner = new Scanner(in); 
					Integer width = Integer.valueOf(scanner.nextLine());
					while(scanner.hasNextLine()){
						String lineStr = scanner.nextLine();
						String [] charAndRepeatCount = lineStr.split(",");
						Integer targetChar =  Integer.valueOf(charAndRepeatCount[0]);
						int repeatCount = Integer.valueOf(charAndRepeatCount[1]);
						for(int i = 0; i < repeatCount ; i++){
							chars.add(targetChar);
						}
					}
					scanner.close();
					setFrameChar(chars, '+');
					addCustomContet(chars, '-', line1, '=');
					addCustomContet(chars, '#', line2, ' ');
					addCustomContet(chars, '?', line3, ' '); 
					print(chars, width);
				} catch (Exception e) {
					e.printStackTrace();
				} 
		    }
		    
		  
		    
		    private static void print(List<Integer> chars, int length){
		    	for(int i = 0 ; i< chars.size(); i++){
		    		print(String.valueOf((char)chars.get(i).byteValue()));
		    		if((i+1) % length ==0){
		    			println("");
		    		}
		    	}
		    }
		    
		    
		    private static void addCustomContet(List<Integer> chars, char placeHolderChar, String customString, char blankChar){
		    	StringBuilder sb = new StringBuilder();  
		    	int start = -1;
		    	for(int i =0; i < chars.size(); i++){
		    		if(chars.get(i)==placeHolderChar){
		    			if(start == -1){
		    				start = i;
		    			}
		    			sb.append(placeHolderChar); 
		    		}
		    	}
		    	String customPlaceholder = sb.toString();
		    	int customSize = customPlaceholder.length();
		    	if(customString.length() > customSize){
		    		customString = customString.substring(0, customSize);
		    	}
		    	int blankSize = customSize - customString.length();
		    	int foreBlankSize = blankSize/2;
		    	int tailBlankSize = blankSize - foreBlankSize;
		    	customString = StringUtil.getRepeatCharString(blankChar, foreBlankSize) + customString + StringUtil.getRepeatCharString(blankChar, tailBlankSize);
		    	for(int i = start; i < start + customSize;  i ++){
		    		chars.set(i, (int) customString.charAt(i-start)); 
		    	}
		    } 
		    
		    private static void setFrameChar(List<Integer> chars, char targetChar){
		    	if(targetChar == '-' || targetChar == '#' || targetChar == '?'){
		    		AnoleLogger.info("Invalid frame char. It can not be '-', '#' or '?'"); 
		    	}
		    	for(int i=0; i<chars.size() ; i++){
		    		if(chars.get(i)=='.'){
		    			chars.set(i, (int)targetChar);
		    		}
		    	}
		    } 
	}

}
