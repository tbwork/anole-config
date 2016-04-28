package org.anole.subscriber.client;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TestHI {
  
	
   public static void main(String[] args) {
	 
		   File f = new File("/D:/Workspaces/LcbArch/Anole.w.oschina/anole/anole-client/anole-subscriber-client/target/classes/");
		   if(f.exists()){
			   System.out.println("yes");
		   }
		   else
			   System.out.println("Not exists");
	   
   }
}
