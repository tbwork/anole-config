package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
@Data
public class PingMessage extends Message{

	public PingMessage(){
		super(MessageType.S2C_PING);
	}
	
}
