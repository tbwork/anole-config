package com.github.tbwork.anole.loader.util;

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

}
