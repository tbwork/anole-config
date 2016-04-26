package org.anole.subscriber.client;

public class Test {
    public static Object lock_wait = new Object();
    public static Object lock_main = new Object();
    public static int count  = 0;
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        Thread1 thread1_1 = new Thread1();
        Thread2 thread2 = new Thread2();
         
        thread1.start();
        thread1_1.start();
         
        try {
            Thread.sleep(200);
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
        		System.out.println("线程"+Thread.currentThread().getName()+"获得了lock_main锁");
	            synchronized (lock_wait) {
	            	System.out.println("线程"+Thread.currentThread().getName()+"获取到了lock_wait锁");
	                try {
	                	System.out.println("线程"+Thread.currentThread().getName()+"进入等待");
	                	lock_wait.wait();
	                    System.out.println("线程"+Thread.currentThread().getName()+"等待结束");
	                } catch (InterruptedException e) {
	                }
	                 
	            }
	            System.out.println("线程"+Thread.currentThread().getName()+"释放了lock_main锁");
        	}
        }
    }
     
    static class Thread2 extends Thread{
        @Override
        public void run() {
        	Thread.currentThread().setName("B"); 
	            synchronized (lock_wait) {
	            	System.out.println("线程"+Thread.currentThread().getName()+"获得了lock_wait锁");
	            	lock_wait.notifyAll();
	                System.out.println("线程"+Thread.currentThread().getName()+"调用了object.notifyAll()");
	            }
	            System.out.println("线程"+Thread.currentThread().getName()+"释放了lock_main锁");
        	} 
    }
}