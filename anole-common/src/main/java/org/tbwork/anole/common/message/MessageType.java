package org.tbwork.anole.common.message;

public enum MessageType {

	S2C_AUTH_FIRST, // 服务端要求客户端先验证身份
	S2C_AUTH_PASS,  // 通知客户端通过了身份验证并且分配token
	S2C_AUTH_FAIL_CLOSE,  
	S2C_MATCH_FAIL, // 服务器端清除了某个Client的时候，再使用clientId+Token将无法登入，这之后重连即可。
	S2C_PING_ACK,
	S2C_RETURN_CONFIG, 
	S2C_REGISTER_CLIENT,
	S2C_MODIFY_RESULT,
	S2C_CONFIG_CHANGE_NOTIFY_B_2_W, //配置变动时，boss通知给worker
	S2C_CONFIG_CHANGE_NOTIFY_W_2_C, //配置变动时，worker通知给client
	 
	C2S_EXIT_CLOSE, //客户端应用程序关闭时，通知服务器自己将主动关闭链接
	C2S_PING,  
	C2S_GET_CONFIG,
	C2S_MODIFY_CONFIG,
	C2S_COMMON_AUTH, // 客户端发送身份信息给服务端 
	C2S_REGISTER_RESULT,
	C2S_CONFIG_CHANGE_NOTIFY_ACK_W_2_B, //Worker收到配置变动消息时，确认收到， worker ack给 boss
	C2S_CONFIG_CHANGE_NOTIFY_ACK_C_2_W, //Subscriber收到配置变动消息时，确认收到，subscriber ack给 worker
	;
	
}
