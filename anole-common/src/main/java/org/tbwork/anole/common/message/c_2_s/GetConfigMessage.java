package org.tbwork.anole.common.message.c_2_s;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
@Data
public class GetConfigMessage extends Message{

	private String key;
	public GetConfigMessage(){
		super(MessageType.C2S_GET_CONFIG);
	}
	public GetConfigMessage(String key){
		super(MessageType.C2S_GET_CONFIG);
		this.key = key;
	}
	
}
