package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
@Data
public class AuthFailAndCloseMessage extends Message {

	private String note;
	public AuthFailAndCloseMessage(){
		super(MessageType.S2C_AUTH_FAIL_CLOSE);
	}
	
}
