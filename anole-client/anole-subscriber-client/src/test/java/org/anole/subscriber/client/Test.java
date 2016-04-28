package org.anole.subscriber.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	public static final Logger logger  = LoggerFactory.getLogger(Test.class);
    public static Object lock_wait = new Object();
    public static Object lock_main = new Object();
    
    
    public static Map<String,String> map = new HashMap<String,String>();
    public static int count  = 0;
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        Thread1 thread1_1 = new Thread1();
        Thread2 thread2 = new Thread2();
        
        map.put("1", "2");
         
        thread1.start();
       // thread1_1.start();
         
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         
        thread2.start();
    }
     
    static class Thread1 extends Thread{ 
        @Override
        public void run() {
        	Thread.currentThread().setName("A"+(++count));
        	synchronized(lock_main)
        	{
        		logger.info("线程"+Thread.currentThread().getName()+"获得了lock_main锁");
	            synchronized (map.get("1")) {
	            	logger.info("线程"+Thread.currentThread().getName()+"获取到了lock_wait锁");
	                try {
	                	logger.info("线程"+Thread.currentThread().getName()+"进入等待");
	                	map.get("1").wait(5000);
	                	logger.debug("语句块内，wait之后");
	                	logger.info("线程"+Thread.currentThread().getName()+"等待结束");
	                } catch (InterruptedException e) {
	                }
	            } 
	            logger.debug("语句块外，wait之后");
	            logger.info("线程"+Thread.currentThread().getName()+"释放了lock_wait锁");
        	}
        	logger.info("线程"+Thread.currentThread().getName()+"释放了lock_main锁");
        }
    }
     
    static class Thread2 extends Thread{
        @Override
        public void run() {
        	Thread.currentThread().setName("B"); 
	            synchronized (map.get("1")) {
	            	logger.info("线程"+Thread.currentThread().getName()+"获得了lock_wait锁");
	            	try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	map.get("1").notifyAll();
	            	logger.info("线程"+Thread.currentThread().getName()+"调用了object.notifyAll()");
	            }
	            logger.info("线程"+Thread.currentThread().getName()+"释放了lock_wait锁");
        	} 
    }
}