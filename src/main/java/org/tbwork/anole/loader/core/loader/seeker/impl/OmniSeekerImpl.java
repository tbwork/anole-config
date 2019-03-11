package org.tbwork.anole.loader.core.loader.seeker.impl;

import org.tbwork.anole.loader.core.loader.seeker.OmniSeeker;
import org.tbwork.anole.loader.exceptions.BadFileException;
import org.tbwork.anole.loader.exceptions.BadJarFileException;
import org.tbwork.anole.loader.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Support Jar and General Directory.
 * @author tbwork
 */
public class OmniSeekerImpl implements OmniSeeker{

	private List<String> candidatePathPatterns;

    public OmniSeekerImpl(String ... candidatePathPatterns){
        this.candidatePathPatterns = new ArrayList<String>();
        for(String item : candidatePathPatterns) {
            this.candidatePathPatterns.add(uniformAbsolutePath(item));
        }
    }
    public OmniSeekerImpl(List<String> candidatePathPatterns){
        this.candidatePathPatterns = new ArrayList<String>();
        for(String item : candidatePathPatterns) {
            this.candidatePathPatterns.add(uniformAbsolutePath(item));
        }
    }
	
	@Override
	public Map<String, InputStream> seekFiles() {
		String rootDirectory = getCommonDirectoryPath();
        if(rootDirectory.trim().length() > 1 && rootDirectory.endsWith("/"))
            rootDirectory = rootDirectory.substring(0, rootDirectory.length()-1);
        if(rootDirectory.endsWith(".jar") ) {
            return seekJar(rootDirectory);
        } else {
            return seekDirectory(rootDirectory);
        }
	}


	private Map<String, InputStream> seekJar(String jarFilePath){
        Map<String, InputStream> result = new HashMap<String, InputStream>();
        long tempTime = System.currentTimeMillis();
        JarFile file = null;
	     try {
	         file = new JarFile(jarFilePath);
	     }
	     catch(Exception e) {
	    	 throw new BadJarFileException();
	     } 
	     Enumeration<JarEntry> entrys = file.entries();
         int foundCount = 0;
         while(entrys.hasMoreElements()){
            JarEntry fileInJar = entrys.nextElement();
            String fileInJarName = fileInJar.getName();
            String fullPath = StringUtil.concat(fileToDirectory(jarFilePath), fileInJarName);
            if(fileInJarName.endsWith(".jar")) {
                // It is another jar
                InputStream jarInputStream = IOUtil.getInputStream(file, fileInJar);
                result.putAll( getConfigInputStreamsFromJar(jarInputStream, fullPath));
                continue;
            }
            if(match(fullPath)){
                foundCount ++;
                InputStream tempStream = IOUtil.getCopiedInputStream(file, fileInJar);
                result.put(fullPath, tempStream);
            }
         }
         AnoleLogger.debug("{} is scanned. {} files are matched. Cost {} ms. ", jarFilePath, foundCount, System.currentTimeMillis() - tempTime);
	     return result;
	}


	private boolean match(String candidate){
        for(String pattern : candidatePathPatterns){
            if(PathUtil.asteriskMatchPath(pattern, candidate))
                return true;
        }
        return false;
    }
	

	private Map<String, InputStream> seekDirectory(String directoryPath){
        Map<String,InputStream> result = new HashMap<String, InputStream>();
        List<File> files = getFilesInDirectory(directoryPath);
        for(File file : files) {
            String fileAbsolutePath = uniformAbsolutePath(file.getAbsolutePath());
            if (fileAbsolutePath.endsWith(".jar")) {
                result.putAll(seekJar(fileAbsolutePath));
            } else {
                result.put(fileAbsolutePath, getFileInputStream(file));
            }
        }
	    return result;
	}


    public List<File> getFilesInDirectory(String path){
        List<File> result = new ArrayList<File>();
        File file = new File(path);
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            File tempFile = fileList[i];
            String filename = uniformAbsolutePath(tempFile.getAbsolutePath());
            if(tempFile.isDirectory()){
                if(preMatchPattern(filename)){
                    result.addAll(getFilesInDirectory(filename));
                }
            }
            else if( filename.endsWith(".jar") ){
                if(preMatchPattern(filename)) {
                    result.add(tempFile); // for further process
                }
            }
            else if(match(filename)){
                result.add(tempFile);
            }
        }
        return result;
    }

    private boolean preMatchPattern(String partialPath){
        for(String item : candidatePathPatterns){
            if(PathUtil.preMatchPattern(partialPath, item))
                return true;
        }
        return false;
    }


	private InputStream getFileInputStream(File file)  {
        try {
            return  new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BadFileException();
        }
    }

    private JarFile getJarFile(String jarPath) {
        JarFile file;
        try {
            file = new JarFile(jarPath);
        }
        catch(FileNotFoundException e) {
            return null;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new BadJarFileException(jarPath);
        }
        return file;
    }
    
    private String getCommonDirectoryPath(){

        String commonPart = "/";

        while(true){
        	String candidate = null;
            for(String item : candidatePathPatterns){
                item = item.substring(commonPart.length());
                if(candidate == null) {
                	candidate = getNextFileName(item);
                }
                if(!candidate.equals(getNextFileName(item)) || isAsterisk(candidate)) {
                	return commonPart;
                }
            }
            commonPart = StringUtil.concat(commonPart, candidate);
        } 
    }

    /**¶
     * @param candidate like "mz-*&#47;"
     * @return true if the candidate contains "*", otherwise return false
     */
    private boolean isAsterisk(String candidate){
        return candidate.contains("*");
    }
    private String getNextFileName(String candidate){
        int index = candidate.indexOf('/');
        if(index > -1){
            return candidate.substring(0, index + 1);
        }
        else{
            return candidate;
        }
    }
    
    private Map<String,InputStream> getConfigInputStreamsFromJar(InputStream jarFileInputStream, String jarPath){
        Map<String,InputStream> result = new HashMap<String, InputStream>();
        try {
            ZipInputStream jarInputStream = new ZipInputStream(jarFileInputStream);
            ZipEntry zipEntry = null;
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {
                String fileInZipName = zipEntry.getName();
                fileInZipName = PathUtil.format2Slash(fileInZipName);
                String fullPath = StringUtil.concat(fileToDirectory(jarPath), fileInZipName) ;
                if(fileInZipName.endsWith(".jar")){
                    InputStream zipFileStream = IOUtil.getZipInputStream(jarInputStream, zipEntry);
                    result.putAll(getConfigInputStreamsFromJar(zipFileStream, fullPath));
                }
                else{
                    if(match(fullPath)){
                        InputStream zipFileStream = IOUtil.getZipInputStream(jarInputStream, zipEntry);
                        result.put(fullPath , zipFileStream);
                    }
                }
            }
        }
        catch(Exception e) {
            AnoleLogger.error("Fail to get configuration file from jar due to {}", e.getMessage());
            throw new BadJarFileException();
        }
        return result;
    }

    private String fileToDirectory(String path){
        if(!path.endsWith("/"))
            path = StringUtil.concat(path,"/");
        return path;
    }

    private static String uniformAbsolutePath(String absolutePath) {
        String result =  PathUtil.getNakedAbsolutePath(PathUtil.toLinuxStylePath(absolutePath));
        result = result.replace("!/", "/");
        if(!result.startsWith("/"))
            return "/"+result;
        return result;
    }
}
