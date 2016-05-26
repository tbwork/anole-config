package org.tbwork.anole.common.message;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public abstract class Message implements Serializable {  
	
	/**
	 * Type of Message. 
	 */ 
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private MessageType msgType;
	
	public Message(){};
	
	public Message(MessageType msgType){
		this.msgType = msgType;
	}
	
	/**
	 * Every custom Message should have this method.
	 */
	public MessageType getType()
	{
		return msgType;
	}
	
}
