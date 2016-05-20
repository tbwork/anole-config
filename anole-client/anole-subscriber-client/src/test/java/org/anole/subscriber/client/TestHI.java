package org.anole.subscriber.client;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TestHI {
   
   public static class A{
	   
	   public void realSay(){
		   System.out.println("AAAA");
	   }
	   public void say(){
		   realSay ();
	   }
	   
   }
   
   public static class B extends A{
	   public void realSay(){
		   System.out.println("BBBB");
	   }
   }
	
	
   public static void main(String[] args) {
	 
		   B b = new B();
		   b.say();
	   
   }
}
