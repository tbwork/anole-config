package org.tbwork.anole.common.message.c_2_s.worker_2_boss;

import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;

import lombok.Data;

@Data
public class W2BChangeNotifyAckMessage extends C2SMessage{

	private String key;
	private long timestamp;
	public W2BChangeNotifyAckMessage(){
		super(MessageType.C2S_CONFIG_CHANGE_NOTIFY_ACK_W_2_B);
	}
	
}
