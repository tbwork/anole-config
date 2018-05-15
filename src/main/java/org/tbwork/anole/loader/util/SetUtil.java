package org.tbwork.anole.loader.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import com.alibaba.fastjson.JSON;

public class SetUtil {

	public static <T> List<T> newArrayList(T ... ts){
		List<T>  result = new ArrayList<T>();
		for(T t: ts) {
			result.add(t);
		}
		return result;
	}
	
	
	public static String [] copyArray(String [] target, int start, int end) {
		String [] result = new String[end - start];
		for(int i = start ; i < end; i++) {
			result[i-start] = target[i];
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		
		 String [] as = {"1","2","3"};
		 System.out.println(JSON.toJSONString(copyArray(as,1,2)));
	}
	
}
