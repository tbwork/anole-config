package org.tbwork.anole.hub.server.util;
import java.util.Random;

import org.tbwork.anole.common.enums.ClientType;

import lombok.Data;

public class ClientInfoGenerator {

	 public static volatile int workerClientId = 0 ;
	 public static volatile int publisherClientId = 0 ;
	 public static volatile int subscriberClientId = 0 ;
	 public static final Object workerLock = new Object();
	 public static final Object publisherLock = new Object();
	 public static final Object subscriberLock = new Object();
 
	 @Data
     public static class ClientInfo{
		 private int clientId;
		 private int token;
     }
	 
	 public static ClientInfo generate(ClientType clientType)
	 {
		 ClientInfo result = new ClientInfo();
		 
		 synchronized(getLock(clientType))
		 {
			 result.setClientId( increaseClientId(clientType) );
			 result.setToken(generateToken());
		 }
		 return result;
	 }
	 
	 private static int increaseClientId(ClientType clientType){
		 switch(clientType){
			 case PUBLISHER: ++publisherClientId; return publisherClientId;
			 case SUBSCRIBER: ++subscriberClientId; return subscriberClientId;
			 case WORKER: ++ workerClientId; return workerClientId;
			 default : return -1;
		 } 
	 }
	 
	 private static Object getLock(ClientType clientType){
		 switch(clientType){
		 case PUBLISHER:  return publisherLock;
		 case SUBSCRIBER: return subscriberLock;
		 case WORKER: return workerLock;
		 default : return null;
	 } 
	 }
	 
	 private static int generateToken()
	 {
		 long nowtime = System.currentTimeMillis();
		 Random random = new Random(nowtime);
		 return random.nextInt(999999999);
	 }
	
	
}
