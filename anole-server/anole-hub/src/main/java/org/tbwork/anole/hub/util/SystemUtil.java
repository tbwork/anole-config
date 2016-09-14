package org.tbwork.anole.hub.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SystemUtil {

	 private static final int start_port = 11300;
	 public static boolean isPortUsing(int port){ 
        boolean flag = false;   
        try {  
        	InetAddress theAddress = InetAddress.getByName("127.0.0.1");  
            Socket socket = new Socket(theAddress,port);  
            flag = true;
        }
        catch (UnknownHostException e){
        	
        }
        catch (IOException e) {  
              
        }  
        return flag;  
    }  
	 
	public static int getOneValidPort(){
		for(int i = start_port; i < 65535; i++){
			if(!isPortUsing(i)) 
				return i;
		}
		throw new RuntimeException("No available port!");
	}

	public static String getLanIp(){
		try{
			InetAddress ia = InetAddress.getLocalHost();  
	        return ia.getHostAddress();  
		}
		catch(Exception e){
			return "127.0.0.1";
		}
	}
}
