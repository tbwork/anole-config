package org.tbwork.anole.common.message.s_2_c;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;

@Data
public class AuthPassWithTokenMessage extends Message {

	public AuthPassWithTokenMessage()
	{
		super(MessageType.S2C_AUTH_PASS);
	} 
}
