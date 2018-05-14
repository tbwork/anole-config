package org.tbwork.anole.loader.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class SetUtil {

	public static <T> List<T> newArrayList(T ... ts){
		List<T>  result = new ArrayList<T>();
		for(T t: ts) {
			result.add(t);
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		
		JarFile jf = new JarFile("D:/test/hapi/hapi-saas-fours-1.0-SNAPSHOT.jar!/BOOT-INF!/lib!/log4j-api-2.7.jar");
		System.out.println(jf.entries());
		
	}
	
}
