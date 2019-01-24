package org.tbwork.anole.loader.core.loader.impl;

import lombok.Data;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.BadJarFileException;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.util.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AnoleFileLoader implements AnoleLoader{ 
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private ConfigManager cm;

	public static String [] includedJarFilters = null;

	private static final List<String> projectInfoPropertiesInJarPathList  = new ArrayList<String>();
	private static final List<String> projectInfoPropertiesPathList  = new ArrayList<String>();
	static {
		projectInfoPropertiesPathList.add("../maven-archiver/pom.properties");
		projectInfoPropertiesPathList.add("META-INF/maven/*/*/pom.properties");
		projectInfoPropertiesInJarPathList.add("META-INF/maven/*/*/pom.properties");
	}
	
	public AnoleFileLoader(){
		cm = SingletonFactory.getLocalConfigManager();
	}
	
	public AnoleFileLoader(ConfigManager cm){
		this.cm = cm ;
	}
	

	@Override
	public Map<String,FileLoadStatus> load() {
		 throw new OperationNotSupportedException();
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
	public Map<String,FileLoadStatus> load(String... configLocations) { 
		Map<String,FileLoadStatus> result = new HashMap<String, FileLoadStatus>();  
		AnoleApp.setRuningInJar(ProjectUtil.getCallerClasspath().contains(".jar!"));
		LogoUtil.decompress("/logo.cps",  "::Anole Loader::   (v1.2.6)");
		AnoleLogger.flush();
		AnoleLogger.debug("Current enviroment is {}", AnoleApp.getEnvironment());
		List<CandidateConfigPath> candidates = new ArrayList<CandidateConfigPath>();
	    // set loading order
		for(String configLocation : configLocations) {
			if(!isInValidScanJar(configLocation)) continue;
			if(configLocation.contains(".jar/") && !configLocation.startsWith(ProjectUtil.getCallerClasspath())) {
				// outer jars
				candidates.add(new CandidateConfigPath(1, configLocation.trim()));
			}
			else if(!configLocation.startsWith(ProjectUtil.getCallerClasspath())){
				// outer directory
				candidates.add(new CandidateConfigPath(50, configLocation.trim()));
			}
			else {
				// main classpath (in jar or in classes)
				candidates.add(new CandidateConfigPath(99, configLocation.trim()));
			}
		} 
		for(String projectInfoFile : getFullPathForProjectInfoFiles()) {
			candidates.add(new CandidateConfigPath(10, projectInfoFile.trim()));
		} 
		List<ConfigInputStreamUnit> configInputStreamUnits = new ArrayList<ConfigInputStreamUnit>();
		for(CandidateConfigPath configLocation : candidates) {
			LoadFileResult  lfr = loadFile(configLocation);
			configInputStreamUnits.addAll(lfr.getCandidateStreams()); 
			result.put(configLocation.getFullPath(),  lfr.fileLoadStatus); 
		}  
		parseFiles(configInputStreamUnits);
		cm.postProcess(); 
		Anole.initialized = true; 
		AnoleLogger.info("[:)] Anole configurations are loaded succesfully.");
		return result;
	}
	

	protected boolean isInValidScanJar(String configLocation){
		if(!configLocation.contains(".jar"))
			return true;
		for(String item : includedJarFilters){
			item = StringUtil.concat("*", item, ".jar*");
			if(StringUtil.asteriskMatch(item, configLocation)){
				return true;
			}
		}
		return false;
	}

	private List<String> getFullPathForProjectInfoFiles() {
		List<String> result = new ArrayList<String>();
		String projectInfoPath =  ProjectUtil.getCallerClasspath(); 
		projectInfoPath = projectInfoPath.replace("test-classes", "classes");
		if(projectInfoPath.contains(".jar!/")) {
			int index = projectInfoPath.indexOf(".jar!/");
			projectInfoPath = projectInfoPath.substring(0, index+6);
			for(String projectInfoFile : projectInfoPropertiesInJarPathList) {
				result.add(projectInfoPath + projectInfoFile);  
			} 
		}
		else {
			for(String projectInfoFile : projectInfoPropertiesPathList) {
				result.add(projectInfoPath + projectInfoFile);  
			} 
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
		AnoleLogger.debug("Searching config files matchs '{}'", ccp.getFullPath());
		if(ccp.getFullPath().contains("jar!/") && ccp.getFullPath().contains("*")){
			String fullpath = ccp.getFullPath();
			String solidDirectory = FileUtil.getSolidDirectory(fullpath);
			List<File> files = FileUtil.getFilesInDirectory(solidDirectory);
			files.addAll(files);


		}
		if(ccp.getFullPath().contains("!/")){ // For Jar projects 
			return loadFileFromJar(ccp);
		}
		else{ 
			return loadFileFromDirectory(ccp);
		}
	}  

	private static boolean isProjectInfo(String fileFullPath) {
		for(String path : projectInfoPropertiesInJarPathList) {
			if(fileFullPath.contains(path))
				return true;
		}
		for(String path : projectInfoPropertiesPathList) {
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
				AnoleLogger.info("Use asterisk in directory is not recommended, e.g., D://a/*/*.txt. We hope you know that it will cause plenty cost of time to seek every matched file.");
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
		String result =  FileUtil.getNakedAbsolutePath(FileUtil.format2Slash(absolutePath));
		if(!result.startsWith("/"))
			return "/"+result;
		return result;
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
		    
		 public static void decompress(String filePath, String message){
			    InputStream in = AnoleFileLoader.class.getResourceAsStream(filePath);  
		    	Scanner scanner = null;
		    	List<Integer> chars = new ArrayList<Integer>();
				try {
					scanner = new Scanner(in); 
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
					setFrameChar(chars, '*');
					addCustomContet(chars, '?', message, ' ');
					print(chars);
				} catch (Exception e) {
					e.printStackTrace();
				} 
		    }
		    
		    private static void print(List<Integer> chars){
		    	for(int i = 0 ; i< chars.size(); i++){
		    		String temp = String.valueOf((char)chars.get(i).byteValue()); 
		    		if(temp.contains("\n")) {
		    			System.out.println("");
		    		}else {
		    			System.out.print(temp);
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
		    	   System.out.println("Invalid frame char. It can not be '-', '#' or '?'"); 
		    	}
		    	for(int i=0; i<chars.size() ; i++){
		    		if(chars.get(i)=='*'){
		    			chars.set(i, (int)targetChar);
		    		}
		    	}
		    }  
		
	}

}
