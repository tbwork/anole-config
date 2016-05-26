package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
@Data
public class PingAckMessage  extends Message{
	
	private int intervalTime;
	public PingAckMessage(){
		super(MessageType.S2C_PING_ACK);
	}
	
}
