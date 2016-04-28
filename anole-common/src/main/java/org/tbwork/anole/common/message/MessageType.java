package org.tbwork.anole.common.message;

public enum MessageType {

	S2C_AUTH_FIRST, // 服务端要求客户端先验证身份
	S2C_AUTH_PASS,  // 通知客户端通过了身份验证并且分配token
	S2C_AUTH_FAIL_CLOSE,  
	S2C_MATCH_FAIL, //
	S2C_PING_ACK,
	S2C_RETURN_CONFIG,
	
	
	C2S_AUTH_BODY, // 客户端发送身份信息给服务端
	C2S_EXIT_CLOSE, //客户端应用程序关闭时，通知服务器自己将主动关闭链接
	C2S_PING,  
	C2S_GET_CONFIG,
	
	
	
}
