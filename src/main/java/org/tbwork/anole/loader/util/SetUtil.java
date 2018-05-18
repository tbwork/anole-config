package org.tbwork.anole.loader.util;

import java.util.ArrayList;
import java.util.List;

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
	 
}
