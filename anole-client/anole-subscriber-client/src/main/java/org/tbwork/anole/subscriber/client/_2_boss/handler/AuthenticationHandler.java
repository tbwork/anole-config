package org.tbwork.anole.subscriber.client._2_boss.handler;

import java.util.concurrent.TimeUnit;
 
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler; 
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;  
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CommonAuthenticationMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
import org.tbwork.anole.common.message.s_2_c.boss.AssignedWorkerInfoMessage;
import org.tbwork.anole.loader.core.AnoleLocalConfig;
import org.tbwork.anole.subscriber.client._2_boss.IAnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_boss.impl.AnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.util.GlobalConfig; 

public class AuthenticationHandler extends  SimpleChannelInboundHandler<Message>  {

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

	private AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	private IAnoleAuthenticationClient anoleAuthenticationClient = AnoleAuthenticationClient.instance();
	
	public AuthenticationHandler(){
		super(false);
	} 
	private CommonAuthenticationMessage getAuthInfo(){
		CommonAuthenticationMessage authBody=new CommonAuthenticationMessage();
    	authBody.setUsername(AnoleLocalConfig.getProperty("client.username","tangbo"));
    	authBody.setPassword(AnoleLocalConfig.getProperty("client.password", "123")); 
    	return authBody;
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception { 
		if(logger.isDebugEnabled())
		     logger.debug("New message received (type = {})", msg.getType());
		MessageType msgType=  msg.getType(); 
        switch (msgType){
	        case S2C_AUTH_FIRST:{ //Please login first. 
	        	ctx.writeAndFlush(getAuthInfo()); 
		      	ReferenceCountUtil.release(msg);
	        } break;
	        case S2C_AUTH_FAIL_CLOSE:{
		      	logger.error("[:(] Username or password is invalid, please check them and try again.");  
		      	ReferenceCountUtil.release(msg);
			} break;
		 	case S2C_AUTH_PASS:{ 
		 		logger.info ("[:)] Login successfully."); 
		 		AssignedWorkerInfoMessage authenResult = ((AssignedWorkerInfoMessage)msg);
		 		anoleSubscriberClient.setWorkerServer(
		 				authenResult.getIp(),
		 				authenResult.getPort(),
		 				authenResult.getClientId(), 
		 				authenResult.getToken()); 	
		 		synchronized(AnoleAuthenticationClient.lock){
		 			AnoleAuthenticationClient.lock.notifyAll();
		 		} 
		 		ReferenceCountUtil.release(msg);
		 	} break; 
	        default:{ 
	        } break;
        }
        // pass to next
        ctx.fireChannelRead(msg); 
		
	}
    
}
