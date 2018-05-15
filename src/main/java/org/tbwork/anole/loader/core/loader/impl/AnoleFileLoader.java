package org.tbwork.anole.loader.core.loader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.tbwork.anole.loader.util.IOUtil;
import org.tbwork.anole.loader.util.ProjectUtil;
import org.tbwork.anole.loader.util.SingletonFactory;
import org.tbwork.anole.loader.util.StringUtil;

import lombok.Data; 

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
	
	@Data
	private static class ConfigInputStreamUnit{
		private String fileName;
		private InputStream is;
		private Integer order;
		public ConfigInputStreamUnit() { 
		}
		public ConfigInputStreamUnit(String fileName, InputStream is, int order) { 
			this.fileName = fileName; 
			this.is = is;
			this.order = order;
		}
		
		public ConfigInputStreamUnit(Entry<String,InputStream> mapEntry, int order) { 
			this.fileName = mapEntry.getKey();
			this.is = mapEntry.getValue();
			this.order = order;
		} 
	}
	
	

	@Data
	public static class CandidateConfigPath { 
		private Integer order;
		private String fullPath;
		public CandidateConfigPath(int order, String configLocation) {
			this.order = order;
			this.fullPath = configLocation;
		}
	}

	
	
	@Override
	public Map<String,FileLoadStatus> load(LogLevel logLevel, String... configLocations) { 
		AnoleLogger.anoleLogLevel = logLevel; 
		Map<String,FileLoadStatus> result = new HashMap<String, FileLoadStatus>();  
		Anole.setRuningInJar(ProjectUtil.getHomeClasspath().contains(".jar!"));
		LogoUtil.decompress("===",  "https://github.com/tbwork/anole-loader", "Version: 1.2.4");
		AnoleLogger.debug("Current enviroment is {}", Anole.getEnvironment());
		Anole.setMainClass(getRootClassByStackTrace());   
		List<CandidateConfigPath> candidates = new ArrayList<CandidateConfigPath>();
	    // set loading order
		for(String configLocation : configLocations) { 
			AnoleLogger.debug(configLocation);
			if(configLocation.contains(".jar/") && !configLocation.startsWith(ProjectUtil.getMainclassClasspath())) {
				// outer jars
				candidates.add(new CandidateConfigPath(1, configLocation.trim()));
			}
			else if(!configLocation.startsWith(ProjectUtil.getMainclassClasspath())){
				// outer directory
				candidates.add(new CandidateConfigPath(3, configLocation.trim()));
			}
			else {
				// main classpath (in jar or in classes)
				candidates.add(new CandidateConfigPath(99, configLocation.trim()));
			}
		} 
		for(String projectInfoFile : getFullPathForProjectInfoFiles()) {
			AnoleLogger.debug(projectInfoFile);
			candidates.add(new CandidateConfigPath(4, projectInfoFile.trim()));
		} 
		List<ConfigInputStreamUnit> configInputStreamUnits = new ArrayList<ConfigInputStreamUnit>();
		for(CandidateConfigPath configLocation : candidates) {
			LoadFileResult  lfr = loadFile(configLocation);
			configInputStreamUnits.addAll(lfr.getCandidateStreams()); 
			result.put(configLocation.getFullPath(),  lfr.fileLoadStatus); 
		}  
		parseFiles(configInputStreamUnits);
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
		String homeClasspath =  ProjectUtil.getHomeClasspath();
		for(String projectInfoFile : projectInfoPropertiesFileList) {
			result.add(homeClasspath + projectInfoFile); 
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
	  
	@Data
	public static class LoadFileResult{ 
		private List<ConfigInputStreamUnit> candidateStreams; 
		private FileLoadStatus fileLoadStatus;
		public LoadFileResult() {
			candidateStreams = new ArrayList<ConfigInputStreamUnit>(); 
		}
	}
	
	/**
	 * @param ccp the absolute path of the configuration file.  
	 */
	protected LoadFileResult loadFile(CandidateConfigPath ccp){
		AnoleLogger.debug("Loading config files matchs '{}'", ccp.getFullPath());
		if(ccp.getFullPath().contains("!/")){ // For Jar projects 
			return loadFileFromJar(ccp);
		}
		else{ 
			return loadFileFromDirectory(ccp);
		}
	}  

	private static boolean isProjectInfo(String fileFullPath) {
		for(String path : projectInfoPropertiesFileList) {
			if(fileFullPath.contains(path))
				return true;
		}
		return false;
	}
	
	private LoadFileResult loadFileFromDirectory(CandidateConfigPath ccp){ 
		LoadFileResult result = new LoadFileResult();
		int fileOrder = ccp.getOrder();
		String fileFullPath = ccp.getFullPath();
		if(!fileFullPath.contains("*")){
			InputStream is = newInputStream(fileFullPath);
			if(is == null) {
				result.setFileLoadStatus(FileLoadStatus.NOT_FOUND);
				return result;
			}  
			result.getCandidateStreams().add(new ConfigInputStreamUnit(fileFullPath, is, fileOrder));
			result.setFileLoadStatus(FileLoadStatus.SUCCESS); 
			return result;
		}
		else
		{  
			if(!isProjectInfo(fileFullPath) && FileUtil.isFuzzyDirectory(fileFullPath)){
				AnoleLogger.warn("Use asterisk in directory is not recomended, e.g., D://a/*/*.txt. We hope you know that it will cause plenty cost of time to seek every matched file.");
			}
			String solidDirectory = FileUtil.getSolidDirectory(fileFullPath);
			File directory = new File(solidDirectory);
			if(!directory.exists()) {
				result.setFileLoadStatus(FileLoadStatus.NOT_MATCHED);
				return result;
			} 
			List<File> files = FileUtil.getFilesInDirectory(solidDirectory);
			boolean matched = false;
			for(File file : files){
				String fileAbsolutePath = uniformAbsolutePath(file.getAbsolutePath());
				if(FileUtil.asteriskMatchPath(fileFullPath,  fileAbsolutePath)){ 
					 result.getCandidateStreams().add(new ConfigInputStreamUnit(fileAbsolutePath, newInputStream(fileAbsolutePath), fileOrder));
					 matched = true;
				}
			}
			result.setFileLoadStatus(matched ? FileLoadStatus.SUCCESS : FileLoadStatus.NOT_MATCHED);
			return result;
		}
	}
	
	private String uniformAbsolutePath(String absolutePath) {
		return FileUtil.getRealAbsolutePath(FileUtil.format2Slash(absolutePath));
	}
	
	private void parseFiles(List<ConfigInputStreamUnit> cisus) {
		ConfigInputStreamUnit [] streams = cisus.toArray(new ConfigInputStreamUnit[cisus.size()]);
		Arrays.sort(streams, new Comparator<ConfigInputStreamUnit>() { 
			@Override
			public int compare(ConfigInputStreamUnit unit1, ConfigInputStreamUnit unit2) {
				return unit1.getOrder() < unit2.getOrder() ? -1 : (unit1.getOrder() == unit2.getOrder() ? 0 : 1);
			} 
		});
		AnoleLogger.debug("{} candidate configuration files are found:", cisus.size());
		if(AnoleLogger.isDebugEnabled()) {
			for(ConfigInputStreamUnit unit : streams)
				AnoleLogger.debug(unit.fileName);
		} 
		for(ConfigInputStreamUnit cisu : streams) {
			AnoleLogger.debug("parsing : {}", cisu.getFileName());
			acfParser.parse(cisu.getIs(), cisu.fileName);
		}
	}
	 
	/**
	 * Input like : /D://prject/a.jar!/BOOT-INF!/classes!/*.properties
	 * @param fileFullPath the full path of the configuration file.
	 * @param ignoreJarInJar whether ignore the jars inner the main jar.
	 * @return
	 */
	private static LoadFileResult loadFileFromJar(CandidateConfigPath ccp){
		LoadFileResult result = new LoadFileResult();
		Integer pathOrder = ccp.getOrder();
		String fileFullPath = ccp.getFullPath();
		String jarPath = ProjectUtil.getJarPath(fileFullPath)+"/"; 
	    String directRelativePath = fileFullPath.replace("!", "").replace(jarPath, "");
	    JarFile file;
		try {
			file = new JarFile(jarPath);
		}
		catch(FileNotFoundException e) {
			if(fileFullPath.contains("*")) {
				//asterisk match
				result.setFileLoadStatus(FileLoadStatus.NOT_MATCHED);
				return result;
			}
			else {
				result.setFileLoadStatus(FileLoadStatus.NOT_FOUND);
				return result;
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
	        if(fileInJarName.endsWith(".jar") && !isProjectInfo(fileFullPath)) {
	        	// It is another jar and the current search is not for project information. 
	        	Map<String,InputStream> inputStreamsMap = getConfigInputStreamsFromJar(IOUtil.getInputStream(file, fileInJar), jarPath+fileInJarName, FileUtil.format2Slash(directRelativePath));
	        	for(Entry<String,InputStream> entry : inputStreamsMap.entrySet()) {
	        		matched = true;
	        		result.getCandidateStreams().add(new ConfigInputStreamUnit(entry, 2));
	        	} 
	        	continue;
	        } 
	        if(FileUtil.asteriskMatchPath(FileUtil.format2Slash(directRelativePath), FileUtil.format2Slash(fileInJarName))){ 
	        	matched = true; 
	        	InputStream tempStream = IOUtil.getCopiedInputStream(file, fileInJar); 
	        	String fullPath = jarPath+fileInJarName; 
	        	result.getCandidateStreams().add(new ConfigInputStreamUnit(fullPath, tempStream, pathOrder)); 
			}
		}      
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if(matched) {
			result.setFileLoadStatus(FileLoadStatus.SUCCESS); 
			return result;
		}
		else {
			result.setFileLoadStatus(fileFullPath.contains("*") ? FileLoadStatus.NOT_MATCHED : FileLoadStatus.NOT_FOUND);
			return result;
		}  
	}
	 
	private static Map<String,InputStream> getConfigInputStreamsFromJar(InputStream jarFileInputStream, String jarName, String directRelativePath){ 
		Map<String,InputStream> result = new HashMap<String, InputStream>();
		try {
			ZipInputStream jarInputStream = new ZipInputStream(jarFileInputStream); 
			ZipEntry zipEntry = null; 
			while ((zipEntry = jarInputStream.getNextEntry()) != null) { 
				String fileInZipName = zipEntry.getName();
				if(FileUtil.asteriskMatchPath(directRelativePath, FileUtil.format2Slash(fileInZipName))){
					String fullPath = jarName+"/"+fileInZipName; 
	    			result.put(fullPath , IOUtil.getZipInputStream(jarInputStream, zipEntry));
	    		} 
			} 
		}
		catch(Exception e) { 
			AnoleLogger.error("Fail to get configuration file from jar due to {}", e.getMessage());
		} 
		return result;
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
