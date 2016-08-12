package org.tbwork.anole.common.message.c_2_s.worker_2_boss;

import org.tbwork.anole.common.message.c_2_s.PingMessage;

import lombok.Data;

@Data
public class WorkerPingMessage extends PingMessage {  
	
	/**
	 * The count of subscriber clients in the worker server.
	 */
	private int subscriberClientCount;
	
	
	/**
	 * The weight of the worker server.
	 */
	private int weight;
	
}
