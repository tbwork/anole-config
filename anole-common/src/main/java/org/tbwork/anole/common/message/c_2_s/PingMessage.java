package org.tbwork.anole.common.message.c_2_s;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
@Data
public class PingMessage extends C2SMessage {

	public PingMessage(){
		super(MessageType.C2S_PING);
	}
	
}
