package org.tbwork.anole.subscriber.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
 


import io.netty.channel.ChannelHandler.Sharable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType; 
import org.tbwork.anole.common.message.s_2_c.ConfigChangeNotifyMessage;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.common.message.s_2_c.ReturnConfigMessage;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.client.GlobalConfig;
import org.tbwork.anole.subscriber.core.ConfigManager;

public class OtherLogicHandler  extends SimpleChannelInboundHandler<Message>{

	public OtherLogicHandler(){
		super(true);
	}
	
	static Logger logger = LoggerFactory.getLogger(OtherLogicHandler.class);
	 
	private  AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception { 
		 MessageType msgType = msg.getType(); 
		 switch(msgType)
		 {  
		 	case S2C_PING_ACK:{ 
		 		PingAckMessage paMsg = (PingAckMessage) msg;
		 		processPingAckResponse(paMsg);
		 	} break;
		 	case S2C_RETURN_CONFIG:{ 
		 		ReturnConfigMessage rvMsg = (ReturnConfigMessage) msg;
		 		processConfigResponse(rvMsg);
		 	} break;
		 	case S2C_CHANGE_NOTIFY:{
		 		ConfigChangeNotifyMessage ccnMsg = (ConfigChangeNotifyMessage) msg;
		 		processConfigChangeNotifyMessage(ccnMsg);
		 	} break;
		 	default:{
		 		
		 		
		 	} break; 
		 }  
	}
	
	private void processPingAckResponse(PingAckMessage paMsg){
		int delay = paMsg.getDelayTime();
		GlobalConfig.PING_INTERVAL = delay >0 ? delay : GlobalConfig.PING_INTERVAL; 
	}

	
	private void processConfigResponse(ReturnConfigMessage rvMsg){ 
		String key   = rvMsg.getKey(); 
		String value = rvMsg.getValue();
		logger.info("[:)] Retrieved config (key = {}) from remote server successfully! value = {}", key, value); 
		ConfigType type  = rvMsg.getValueType();
		ConfigManager.setConfigItem(key, value, type);  
	}
	
	private void processConfigChangeNotifyMessage(ConfigChangeNotifyMessage ccnMsg){
		
	} 
}
