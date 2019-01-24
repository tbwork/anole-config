package org.tbwork.anole.loader.util;

import java.util.List;
import java.util.Set;

public class CollectionUtil {

	public static String [] list2StringArray(List<String> list){ 
		return list.toArray(new String[list.size()]);  
	}
	
	public static Integer [] list2IntegerArray(List<Integer> list){
		return list.toArray(new Integer[list.size()]);  
	}
	
	public static Boolean [] list2BooleanArray(List<Boolean> list){
		return list.toArray(new Boolean[list.size()]);  
	} 
	
	public static String [] set2StringArray(Set<String> set){ 
		return set.toArray(new String[set.size()]);  
	}
	
	public static Integer [] set2IntegerArray(Set<Integer> set){
		return set.toArray(new Integer[set.size()]);  
	}
	
	public static Boolean [] set2BooleanArray(Set<Boolean> set){
		return set.toArray(new Boolean[set.size()]);  
	}

	public static String [] mergeArray(String [] arr1, String [] arr2){
		int size = arr1.length + arr2.length;
		String [] result = new String[size];
		int p = 0;
		for(String str : arr1){
			result[p++] = str;
		}
		for(String str : arr2){
			result[p++] = str;
		}
		return result;
	}
}
