package org.tbwork.anole.loader.core.loader.impl;

import lombok.Data;
import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.context.AnoleApp;
import org.tbwork.anole.loader.context.impl.MatchCounter;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.loader.seeker.OmniSeeker;
import org.tbwork.anole.loader.core.loader.seeker.impl.OmniSeekerImpl;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.exceptions.OperationNotSupportedException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

public class AnoleFileLoader implements AnoleLoader{ 
	
	private AnoleConfigFileParser acfParser = AnoleConfigFileParser.instance();
	
	private ConfigManager cm;

	private AnoleCallBack callBack;

	/**
	 * The environment type of current running os.
	 */
	private String sysEnv;

	private static final List<String> projectInfoPropertiesInJarPathList  = new ArrayList<String>();
	private static final List<String> projectInfoPropertiesPathList  = new ArrayList<String>();
	static {
		projectInfoPropertiesPathList.add("../maven-archiver/pom.properties");
		projectInfoPropertiesPathList.add("META-INF/maven/*/*/pom.properties");
		projectInfoPropertiesInJarPathList.add("META-INF/maven/*/*/pom.properties");
	}
	
	public AnoleFileLoader(){
		this(SingletonFactory.getLocalConfigManager());
	}
	
	public AnoleFileLoader(ConfigManager cm){
		if(StringUtil.isNullOrEmpty(AnoleApp.getEnvironment()) ){
			String env = getEnvFromFilename();
			AnoleApp.setEnvironment(env);
		}
		this.cm = cm ;
	}

	@Override
	public void load() {
		 throw new OperationNotSupportedException();
	}  
 
	@Data
	private static class ConfigInputStreamUnit{
		private String fileName;
		private InputStream is;
        /**
         * More larger, process later.
         */
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
	public static class ConfigPathPattern {
        /**
         * More larger, process later.
         */
		private Integer order;
		private String pattern;
		public ConfigPathPattern(int order, String pattern) {
			this.order = order;
			this.pattern = pattern;
		}
	}


	
	
	@Override
	public void load(String... locationPatterns) {
		Map<String,FileLoadStatus> result = new HashMap<String, FileLoadStatus>();
		long currentTime = System.nanoTime();
		AnoleApp.setRuningInJar(ProjectUtil.getCallerClasspath().contains(".jar!"));
		LogoUtil.decompress("/logo.cps",  "::Anole Loader::   (v1.2.9)");
		AnoleLogger.flush();
		AnoleLogger.debug("Current enviroment is {}", AnoleApp.getEnvironment());
		List<ConfigPathPattern> patterns = new ArrayList<ConfigPathPattern>();
	    // set loading order, larger means later process.
		for(String locationPattern : locationPatterns) {
			locationPattern = PathUtil.uniformPath(locationPattern);
			if(locationPattern.contains(".jar/") && !locationPattern.startsWith(ProjectUtil.getCallerClasspath())) {
				// outer jars
				patterns.add(new ConfigPathPattern(1, locationPattern.trim()));
			}
			else if(!locationPattern.startsWith(ProjectUtil.getCallerClasspath())){
				// outer directory
				patterns.add(new ConfigPathPattern(50, locationPattern.trim()));
			}
			else {
				// main classpath (in jar or in classes)
				patterns.add(new ConfigPathPattern(99, locationPattern.trim()));
			}
		}
		for(String projectInfoFile : getFullPathForProjectInfoFiles()) {
			projectInfoFile = PathUtil.uniformPath(projectInfoFile);
			patterns.add(new ConfigPathPattern(10, projectInfoFile.trim()));
		}
		if(AnoleLogger.isDebugEnabled()){
			Collections.sort(patterns, new Comparator<ConfigPathPattern>() {
				@Override
				public int compare(ConfigPathPattern o1, ConfigPathPattern o2) {
					return o1.getOrder().compareTo(o2.getOrder());
				}
			});
			AnoleLogger.debug("Orderred pattern list: ");
			for(ConfigPathPattern pattern : patterns){
				AnoleLogger.debug("[{}] {}", pattern.getOrder(), pattern.getPattern());
			}
		}
		List<ConfigInputStreamUnit> configInputStreamUnits = loadFileMatch(patterns);
		parseFiles(configInputStreamUnits);
		cm.postProcess();
		Anole.initialized = true;
		if(callBack != null){
            callBack.run();
		}
		long timeCost = (System.nanoTime() - currentTime)/ 1000000;
		AnoleLogger.info("[:)] Anole configurations are loaded succesfully in {} ms. ", timeCost);
	}

    /**
     * Set a callback logic which will be called after all configuration files were loaded.
     *
     * @param anoleCallBack
     */
    @Override
    public void setCallback(AnoleCallBack anoleCallBack) {
        this.callBack = anoleCallBack;
    }


    private Integer getJarCountInPath(String path){
    	int count = 0;
    	while(path.contains(".jar")){
    		path = path.replace(".jar", "");
    		count ++ ;
		}
		return count;
	}

    private void parseFiles(List<ConfigInputStreamUnit> cisus) {
		ConfigInputStreamUnit [] streams = cisus.toArray(new ConfigInputStreamUnit[cisus.size()]);
		Arrays.sort(streams, new Comparator<ConfigInputStreamUnit>() {
			@Override
			public int compare(ConfigInputStreamUnit unit1, ConfigInputStreamUnit unit2) {
				if(unit1.getOrder() < unit2.getOrder()){
					return -1;
				}
				else if(unit1.getOrder() > unit2.getOrder()){
					return 1;
				}
				else{
					String path1 = unit1.getFileName();
					String path2 = unit2.getFileName();
					Integer jarCount1 = getJarCountInPath(path1);
					Integer jarCount2 = getJarCountInPath(path2);
					return jarCount2.compareTo(jarCount1);
				}
			}
		});
		AnoleLogger.debug("{} candidate configuration files are found:", cisus.size());
		if(AnoleLogger.isDebugEnabled()) {
			for(ConfigInputStreamUnit unit : streams)
				AnoleLogger.debug("[{}] {}", unit.getOrder(), unit.fileName);
		}
		AnoleLogger.debug("Start to parse upper candidate files...");
		int p = 1;
		for(ConfigInputStreamUnit cisu : streams) {
			AnoleLogger.debug("{} - parsing: {}", p++,  cisu.getFileName());
			acfParser.parse(cisu.getIs(), cisu.fileName);
		}
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
	 * @param patternItems the candidate patterns
	 */
	protected List<ConfigInputStreamUnit> loadFileMatch(List<ConfigPathPattern> patternItems){
		List<String> patterns = new ArrayList<String>();
		for(ConfigPathPattern item : patternItems){
			patterns.add(item.getPattern());
		}
		OmniSeeker omniSeeker = new OmniSeekerImpl(patterns);
		Map<String,InputStream> resultMap = omniSeeker.seekFiles();
		List<ConfigInputStreamUnit> result = new ArrayList<ConfigInputStreamUnit>();
		for(ConfigPathPattern item : patternItems){
			AnoleLogger.debug("Pattern {} matches following files:", item.getPattern());
			AnoleLogger.debug("------------------------------------------------------------------");
			Integer p = 0;
			for(Entry<String, InputStream> entry : resultMap.entrySet()){
				String filepath = entry.getKey();
				if(PathUtil.asteriskMatchPath(item.getPattern(), filepath)){
					AnoleLogger.debug("{} - {}", ++p , filepath);
                    MatchCounter.setFoundFlag(item.getPattern());
					result.add(new ConfigInputStreamUnit(filepath, entry.getValue(), item.getOrder()));
				}
			}
			if(p == 0)
                AnoleLogger.debug("NO MATCHED FILES");
			AnoleLogger.debug("------------------------------------------------------------------");
		}
		return result;
	}  


	static boolean isProjectInfo(String fileFullPath) {
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



	private String getEnvFromFilename(){
		switch(OsUtil.getOsCategory()){
			case WINDOWS:{
				return getEnvFromFilenamePath("C://anole/");
			}
			case LINUX:{
				return getEnvFromFilenamePath("/etc/anole/");
			}
			case MAC:{
				return getEnvFromFilenamePath("/Users/anole/");
			}
			default: return null;
		}
	}

	private String getEnvFromFilenamePath(String directoryPath){
		// check by the following order
		// 1. the system property
		// 2. the JVM boot variable
		// 3. the environment file
		//check if the environment is already set or not
		sysEnv = System.getProperty("anole.runtime.currentEnvironment");
		if(sysEnv == null)
			sysEnv = System.getenv("anole.runtime.currentEnvironment");

		if(sysEnv != null && !sysEnv.isEmpty()) {
			cm.setConfigItem("anole.runtime.currentEnvironment", sysEnv, ConfigType.STRING);
			return sysEnv;
		}

		File file = new File(directoryPath);
		if(file.exists()){
			File [] fileList = file.listFiles();
			for(File ifile : fileList){
				String ifname = ifile.getName();
				if(StringUtil.asteriskMatch("*.env", ifname)){
					sysEnv = ifname.replace(".env", "");
					return sysEnv;
				}
			}
		}
		if(!StringUtil.isNullOrEmpty(AnoleApp.getEnvironment())){
			sysEnv = AnoleApp.getEnvironment();
			return sysEnv;
		}
		//throw new EnvironmentNotSetException();
		// from 1.2.5 use warning instead and return "all" environment.
		AnoleLogger.info("Cound not decide current environment, 'all' environment will be used.");
		sysEnv = "all";
		return sysEnv;
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
