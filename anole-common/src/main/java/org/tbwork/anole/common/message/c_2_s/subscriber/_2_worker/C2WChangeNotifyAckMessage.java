package org.tbwork.anole.common.message.c_2_s.subscriber._2_worker;

import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;

import lombok.Data;

@Data
public class C2WChangeNotifyAckMessage extends C2SMessage{

	private String key;
	private long timestamp;
	public C2WChangeNotifyAckMessage(){
		super(MessageType.C2S_CONFIG_CHANGE_NOTIFY_ACK_C_2_W);
	}
	
}
