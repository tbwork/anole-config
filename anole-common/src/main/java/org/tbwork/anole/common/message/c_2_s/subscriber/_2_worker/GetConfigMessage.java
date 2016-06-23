package org.tbwork.anole.common.message.c_2_s.subscriber._2_worker;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.C2SMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
@Data
public class GetConfigMessage extends C2SMessage {

	private String key;
	private String env; 
	public GetConfigMessage(String key, String env){
		super(MessageType.C2S_GET_CONFIG);
		this.key = key;
		this.env = env;
	}
	
}
