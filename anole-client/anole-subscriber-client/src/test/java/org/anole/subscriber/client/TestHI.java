package org.anole.subscriber.client;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;

public class TestHI {
   
 
	public static final int TIMEOUT = 10;
	
	public static class Lock{
		volatile int result ; 
		volatile boolean flag;
	}
	
	private static final ExecutorService es = Executors.newCachedThreadPool();
	
    public static void main(String[] args) throws Exception {
	
     
    	for(int i =0; i < 1000000;i++){
    		
    		final int j = i;
    		es.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					System.out.println("Thread-"+j+" starts");
					TimeUnit.SECONDS.sleep(10000);
					// TODO Auto-generated method stub
					return null;
				}
			  
    			
    		});
    		
    	}
    	
    }
	
	
    
    public static int fastReturn() throws Exception{
    	
    	  final Lock obj = new Lock(); 
          synchronized(obj){
       	       Future<Void>  r1 = es.submit( new Callable<Void>() { 
       			@Override
       			public Void call() throws Exception {
       				 try{
       					 System.out.println("r1 starts");
           				// TimeUnit.SECONDS.sleep(5);
           				 if(!obj.flag){
           					 synchronized(obj){
           						 if(!obj.flag){
           							 obj.result  =1 ;
                   					 obj.flag = true;
           						 }
               					 obj.notify();
               				 }
           				 }
           				 System.out.println("r1 ends");
           				
       				 }
       				 catch(Exception e){
       					 TimeUnit.SECONDS.sleep(TIMEOUT);
       					 obj.notify();
       				 }
       				 return null;
       			}
       		
       		   });
       	        System.out.println("r1 posts");
       	        Future<Void> r2 = es.submit( new Callable<Void>() { 
   					@Override
   					public Void call() throws Exception {
   						try{
	   						 System.out.println("r2 starts");
	   						// TimeUnit.SECONDS.sleep(1);
	   						 if(!obj.flag){
	   	    					 synchronized(obj){
	   	    						 if(!obj.flag){
	   	    							 obj.result  =2 ;
	   	            					 obj.flag = true;
	   	    						 } 
	   	        					 obj.notify();
	   	        					
	   	        				 }
	   	    				 }
	   						 System.out.println("r2 ends");
	   						 
   					    }catch(Exception e){
   	       					 TimeUnit.SECONDS.sleep(TIMEOUT);
   	       					 obj.notify();
   	       				 }
   						 return null;
   					} 
       		   }); 
       		   System.out.println("r2 posts");
       		   obj.wait();	 
          }
   	     return obj.result;
    }
	
	
}
