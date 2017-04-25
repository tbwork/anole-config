package org.tbwork.anole.common.message.s_2_c.boss._2_worker;

import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;

import lombok.Data;

@Data
public class RegisterClientMessage extends Message {
 
	 public RegisterClientMessage()
	 {
		 super(MessageType.S2C_REGISTER_CLIENT);
	 }
	 
	 private ClientType clientType; 
	 private String enviroment;
}
