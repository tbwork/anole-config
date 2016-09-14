package org.tbwork.anole.common.message.c_2_s.worker_2_boss;

import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.c_2_s.PingMessage;

import lombok.Data;

@Data
public class RegisterResultMessage extends C2SMessage { 
	 
	/**
	 * Different from token in C2SMessage, this is a result
	 * to tell Boss server to forward to the customer clients.
	 */
	private int resultClientId;
	/**
	 * Different from token in C2SMessage, this is a result
	 * to tell Boss server to forward to the customer clients.
	 */
	private int resultToken; 
	
	/**
	 * Worker's ip address in the LAN
	 */
	private String resultIp;
	
	/**
	 * The port which is binded by the worker.
	 */
	private int resultPort;
	/**
	 * ClientType
	 */
	private ClientType resultClientType;
	public RegisterResultMessage(){
		super(MessageType.C2S_REGISTER_RESULT);
	}
	 
}
