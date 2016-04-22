package org.tbwork.anole.hub.server.util;
import java.util.Random;

import lombok.Data;

public class ClientInfoGenerator {

	 public static volatile int clientId = 0 ;
	 
	 @Data
     public static class ClientInfo{
		 private int clientId;
		 private int token;
     }
	 
	 public static ClientInfo generate()
	 {
		 ClientInfo result = new ClientInfo();
		 
		 synchronized(ClientInfoGenerator.class)
		 {
			 result.setClientId( ++ clientId);
			 result.setToken(generateToken());
		 }
		 
		 return result;
	 }
	 
	 
	 private static int generateToken()
	 {
		 long nowtime = System.currentTimeMillis();
		 Random random = new Random(nowtime);
		 return random.nextInt();
	 }
	
	
}
