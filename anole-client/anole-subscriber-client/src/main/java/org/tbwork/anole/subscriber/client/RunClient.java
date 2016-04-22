package org.tbwork.anole.subscriber.client;

public class RunClient {

	
	public static void main(String[] args) {
		
		AnoleSubscriberClient client = new AnoleSubscriberClient();
		client.connect("localhost", 8080); 
	}
	
}
