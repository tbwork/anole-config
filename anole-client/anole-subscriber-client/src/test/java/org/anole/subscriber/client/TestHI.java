package org.anole.subscriber.client;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.KeyGenerator;

public class TestHI {
   
   public static class A{
	    
	   public static A a  = new A();
	   public A(){
		   new B();
	   }
	   public void print(){
		   System.out.println("HHH");
	   }
   } 
	
   public static class B{
	  
   }
	
   public static void main(String[] args) {
	    
	   A.a.print();
   }
    
}
