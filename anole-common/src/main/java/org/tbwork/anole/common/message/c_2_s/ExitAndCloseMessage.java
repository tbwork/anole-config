package org.tbwork.anole.common.message.c_2_s;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
@Data
public class ExitAndCloseMessage extends C2SMessage {

	public ExitAndCloseMessage(){
		super(MessageType.C2S_EXIT_CLOSE);
	}
	
}
