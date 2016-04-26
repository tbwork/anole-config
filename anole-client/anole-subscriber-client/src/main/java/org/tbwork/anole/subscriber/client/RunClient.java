package org.tbwork.anole.subscriber.client;

import org.tbwork.anole.common.message.c_2_s.AuthenticationBodyMessage;
import org.tbwork.anole.common.message.c_2_s.GetConfigMessage;

public class RunClient {

	
	public static void main(String[] args) {
		
		AnoleSubscriberClient client = new AnoleSubscriberClient();
		client.connect("localhost", 8080); 
		
		//AuthenticationBodyMessage message = new AuthenticationBodyMessage("tangbo","123456"); 
		
		//client.socketChannel.writeAndFlush(message);
		
		//client.socketChannel.writeAndFlush(new GetConfigMessage("1"));
	}
	
}
