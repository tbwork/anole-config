package org.tbwork.anole.common.message.c_2_s;

import lombok.Data;

import org.tbwork.anole.common.enums.ClientType; 
import org.tbwork.anole.common.message.MessageType; 
@Data
public class CommonAuthenticationMessage extends C2SMessage {
 
	 public CommonAuthenticationMessage()
	 {
		 super(MessageType.C2S_COMMON_AUTH);
	 }
	 
	 public CommonAuthenticationMessage(String username, String password)
	 {
		 super(MessageType.C2S_COMMON_AUTH);
		 this.username = username;
		 this.password = password;
	 }
	  
	 private String username;
	 
	 private String password;
	
	 private ClientType clientType;
	 
}
