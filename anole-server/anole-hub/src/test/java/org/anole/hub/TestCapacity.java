package org.anole.hub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCapacity {

	private static final Logger logger = LoggerFactory.getLogger(TestCapacity.class);
	
	public static void main(String[] args) {
		
		boolean flag = false;
		Map<Integer,String > map = new ConcurrentHashMap<Integer,String>();
		long st = System.nanoTime();
		for(int i = 0 ;i < 100000;i++){
			map.put(i, i+"");
		}
		long et = System.nanoTime();
		System.out.println(et - st);
		
		st = System.nanoTime();
		for(int i = 0 ;i < 100000;i++){
			map.remove(i);
		}
	    et = System.nanoTime();
		System.out.println(et - st);
		
		st = System.nanoTime();
		for(int i = 0 ;i < 100000;i++){
			flag = !flag;
		}
	    et = System.nanoTime();
		System.out.println(et - st);

	}
	
}
