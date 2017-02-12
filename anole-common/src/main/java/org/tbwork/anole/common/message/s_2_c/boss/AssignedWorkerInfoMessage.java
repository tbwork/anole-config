package org.tbwork.anole.common.message.s_2_c.boss;

import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;

import lombok.Data;

@Data
public class AssignedWorkerInfoMessage extends AuthPassWithTokenMessage{ 
	private String ip;
	private int port; 
	private String message;
	
	public AssignedWorkerInfoMessage(){
		this.setClientId(-1);
		this.setToken(-1);
		this.setIp("null");
		this.setPort(-1);
	}
}
