package org.tbwork.anole.publisher.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.PingAckMessage;
import org.tbwork.anole.common.message.s_2_c.boss._2_publisher.ModifyResultMessage; 
import org.tbwork.anole.publisher.client.ConnectionMonitor;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.StaticClientConfig;
import org.tbwork.anole.publisher.client.impl.AnolePublisherClient;
import org.tbwork.anole.publisher.client.impl.LongConnectionMonitor;
import org.tbwork.anole.publisher.core.AnolePublisher; 

public class OtherLogicHandler  extends SimpleChannelInboundHandler<Message>{

	public OtherLogicHandler(){
		super(true);
	}
	
	static Logger logger = LoggerFactory.getLogger(OtherLogicHandler.class);
	
	private static final ConnectionMonitor lcMonitor = LongConnectionMonitor.instance();
	
	private AnolePublisherClient anolePublisher = AnolePublisherClient.instance();
	
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
		 	case S2C_MODIFY_RESULT:{ 
		 		ModifyResultMessage mrMsg = (ModifyResultMessage) msg;
		 		processModifyResultResponse(mrMsg);
		 	} break;
		 	default:{ 
		 	} break; 
		 }  
	}
	
	private void processPingAckResponse(PingAckMessage paMsg){
		int interval = paMsg.getIntervalTime();
		if(interval > 0 && interval != StaticClientConfig.PING_INTERVAL){
			StaticClientConfig.PING_INTERVAL = interval ;
			StaticClientConfig.PING_DELAY = interval;
			lcMonitor.restart();
			logger.info("Synchronize PING_INTERVAL with the server, new interval is set as {} ms", StaticClientConfig.PING_INTERVAL);
		} 
		anolePublisher.ackPing();
	} 
	 
	
	private void processModifyResultResponse(ModifyResultMessage message){ 
		synchronized(AnolePublisher.writeLock){ 
			AnolePublisher.operationResult = message.getChangeResult();
			AnolePublisher.writing = false;
			AnolePublisher.writeLock.notifyAll(); 
		} 
	}
}
