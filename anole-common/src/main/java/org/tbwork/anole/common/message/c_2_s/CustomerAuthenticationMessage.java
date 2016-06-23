package org.tbwork.anole.common.message.c_2_s;

import lombok.Data;

import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
@Data
public class CustomerAuthenticationMessage extends C2SMessage {

	 
	 public CustomerAuthenticationMessage()
	 {
		 super(MessageType.C2S_CUSTOMER_AUTH);
	 }
	 
	 public CustomerAuthenticationMessage(String username, String password)
	 {
		 super(MessageType.C2S_CUSTOMER_AUTH);
		 this.username = username;
		 this.password = password;
	 }
	  
	 private String username;
	 
	 private String password;
	
	 
}
