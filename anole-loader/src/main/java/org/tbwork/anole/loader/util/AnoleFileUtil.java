package org.tbwork.anole.loader.util;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.exceptions.BadJarFileException;


public class AnoleFileUtil {

	private static final AnoleLogger logger = new AnoleLogger( AnoleFileUtil.class);

	public static FileInputStream getInputStream(File file){
		try {
			return new FileInputStream(file);
		} catch (Exception e) {
			throw new RuntimeException("Something bad happened! Message: " + e.getMessage());
		}
	}


	public static boolean isSpringFatJar(String solidJarPath){
		if(!solidJarPath.contains(".jar")){
			return false;
		}
		JarFile file;
		try {
			file = new JarFile(solidJarPath);
		}
		catch(FileNotFoundException e) {
			throw new RuntimeException(String.format("File '%s' is not existed!", solidJarPath));
		}
		catch(Exception e) {
			throw new BadJarFileException(solidJarPath);
		}
		Enumeration<JarEntry> entrys = file.entries();
		while(entrys.hasMoreElements()){
			JarEntry fileInJar = entrys.nextElement();
			String fileInJarName = fileInJar.getName();
			if(fileInJarName.contains("BOOT-INF")){
				return true;
			}
		}
		return false;
	}


	/**
	 * @param patternedPath like : /D://prject/a.jar!/BOOT-INF!/classes!/*.properties
	 */
	public static Map<String, InputStream> loadFileStreamFromJar(String patternedPath){
		Map<String, InputStream> result = new HashMap<>();
		String jarPath = ProjectUtil.getJarPath(patternedPath)+"/";
		patternedPath = patternedPath.replace("!", "");
		String relativePatternedPath =patternedPath.replace(jarPath, "");
		JarFile file;
		try {
			file = new JarFile(jarPath);
		}
		catch(FileNotFoundException e) {
			return result;
		}
		catch(Exception e) {
			throw new BadJarFileException(jarPath);
		}
		Enumeration<JarEntry> entrys = file.entries();
		while(entrys.hasMoreElements()){
			JarEntry fileInJar = entrys.nextElement();
			String fileInJarName = fileInJar.getName();
			if(fileInJarName.endsWith(".jar")) {
				// It is another jar and the current search is not for project information.
				Map<String,InputStream> inputStreamsMap = getConfigInputStreamsFromJar(IOUtil.getInputStream(file,
						fileInJar), jarPath+fileInJarName, PathUtil.format2Slash(patternedPath));
				result.putAll(inputStreamsMap);
				continue;
			}
			if(PathUtil.asteriskMatch(PathUtil.format2Slash(relativePatternedPath), PathUtil.format2Slash(fileInJarName))){
				InputStream tempStream = IOUtil.getCopiedInputStream(file, fileInJar);
				String fullPath = jarPath+fileInJarName;
				result.put(fullPath, tempStream);
			}
		}
		try {
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	public static Map<String, File> loadFileByPatternedPath(String patternedPath){
		Map<String, File> result = new HashMap<>();
		if(!patternedPath.contains("*")){
			// SOLID PATH
			File file = getFile(patternedPath);
			if(file != null){
				result.put(patternedPath, file);
			}
			return result;
		}

		String solidDirectoryPath = PathUtil.getSolidDirectory(patternedPath);
		File directory = new File(solidDirectoryPath);
		if(!directory.exists()) {
			return result;
		}
		List<File> files = AnoleFileUtil.getFilesInDirectoryWithSpecifiedPattern(solidDirectoryPath, patternedPath);

		for(File file : files){
			result.put(file.getAbsolutePath(), file);
		}
		return result;
	}

	private static List<File> getFilesInDirectoryWithSpecifiedPattern(String solidDirectory, String patternedPath){
		List<File> result = new ArrayList<File>();
		File file = new File(solidDirectory);
		File[] fileList = file.listFiles();
		if(fileList == null){
			return result;
		}
		for (int i = 0; i < fileList.length; i++) {
			File tempFile = fileList[i];
			if(tempFile.isDirectory()){
				if(PathUtil.asteriskPreMatch(patternedPath, tempFile.getAbsolutePath())){
					result.addAll(getFilesInDirectoryWithSpecifiedPattern(tempFile.getAbsolutePath(), patternedPath));
				}
			}
			else {
				if(PathUtil.asteriskMatch(patternedPath, tempFile.getAbsolutePath())){
					result.add(tempFile);
				}
			} 
		} 
		return result;
	}

	private static Map<String,InputStream> getConfigInputStreamsFromJar(InputStream jarFileInputStream,
																		String directoryName,
																		String relativePattern){
		Map<String,InputStream> result = new HashMap<String, InputStream>();
		try {
			ZipInputStream jarInputStream = new ZipInputStream(jarFileInputStream);
			ZipEntry zipEntry = null;
			while ((zipEntry = jarInputStream.getNextEntry()) != null) {
				String fileInZipName = zipEntry.getName();
				String fullPath = directoryName+"/"+fileInZipName;
				if(PathUtil.asteriskMatch(relativePattern, PathUtil.format2Slash(fullPath))){
					result.put(fullPath , IOUtil.getZipInputStream(jarInputStream, zipEntry));
				}
			}
		}
		catch(Exception e) {
			logger.error("Fail to get configuration file from jar due to {}", e.getMessage());
		}
		return result;
	}


	private static File getFile(String filepath){
		File file = new File(filepath);
		if(file.exists()){
			return file;
		}
		return null;
	}

	private static InputStream newInputStream(String filepath){
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

}
