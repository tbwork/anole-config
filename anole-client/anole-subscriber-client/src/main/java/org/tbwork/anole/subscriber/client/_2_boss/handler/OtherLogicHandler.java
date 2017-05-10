package org.tbwork.anole.subscriber.client._2_boss.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
 


import io.netty.channel.ChannelHandler.Sharable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2SConfigChangeNotifyMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.ReturnConfigMessage;
import org.tbwork.anole.subscriber.client._2_worker.ConnectionMonitor;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.LongConnectionMonitor;
import org.tbwork.anole.subscriber.core.impl.SubscriberConfigManager;
import org.tbwork.anole.subscriber.util.GlobalConfig;

public class OtherLogicHandler  extends SimpleChannelInboundHandler<Message>{

	public OtherLogicHandler(){
		super(true);
	}
	
	static Logger logger = LoggerFactory.getLogger(OtherLogicHandler.class);
	 
	private AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	
	private SubscriberConfigManager cm = SubscriberConfigManager.getInstance();
	
	private static final ConnectionMonitor lcMonitor = LongConnectionMonitor.instance();
	
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception { 
		 MessageType msgType = msg.getType(); 
		 switch(msgType)
		 {   
		 	case S2C_RETURN_CONFIG:{ 
		 		ReturnConfigMessage rvMsg = (ReturnConfigMessage) msg;
		 		processConfigResponse(rvMsg);
		 	} break;
		 	default:{ 
		 	} break; 
		 }  
	}
	 
 
	private void processConfigResponse(ReturnConfigMessage rvMsg){ 
		String key   = rvMsg.getKey(); 
		String value = rvMsg.getValue();
		ConfigType type  = rvMsg.getValueType(); 
		if(value!=null) 
			logger.info("[:)] Retrieved config (key = {}) from remote server successfully! value = {}", key, value);   
		else
			logger.warn("[:)] Remote config (key = {}) is not existed.", key); 
		cm.setConfigItem(key, value, type);
	}
}
