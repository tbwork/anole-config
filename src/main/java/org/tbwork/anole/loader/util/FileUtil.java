package org.tbwork.anole.loader.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;


public class FileUtil {




	public static List<File> getFilesInDirectory(String path){
		List<File> result = new ArrayList<File>();
		File file = new File(path);
		File[] fileList = file.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			File tempFile = fileList[i];
			if(tempFile.isDirectory()){
				result.addAll(getFilesInDirectory(tempFile.getAbsolutePath()));
			}
			else {
				result.add(tempFile);
			}
		}
		return result;
	}




	public static JarFile createJarFile(String path){
		JarFile file = null;
		try {
			file = new JarFile(path);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return file;
	}


}
