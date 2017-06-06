package org.tbwork.anole.subscriber.client._2_boss.handler;
 
 
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler; 
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CommonAuthenticationMessage;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_boss.S2BCommonAuthenticationMessage;
import org.tbwork.anole.common.message.s_2_c.boss.AssignedWorkerInfoMessage;
import org.tbwork.anole.loader.core.Anole; 
import org.tbwork.anole.subscriber.client._2_boss.impl.AnoleAuthenticationClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
import org.tbwork.anole.subscriber.core.AnoleClient; 
public class AuthenticationHandler extends  SimpleChannelInboundHandler<Message>  {

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

	private AnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	private AnoleAuthenticationClient anoleAuthenticationClient = AnoleAuthenticationClient.instance();
	
	public AuthenticationHandler(){
		super(false);
	} 
	private CommonAuthenticationMessage getAuthInfo(){
		S2BCommonAuthenticationMessage authBody=new S2BCommonAuthenticationMessage();
    	authBody.setUsername(Anole.getProperty("anole.client.subscriber.username","tangbo"));
    	authBody.setPassword(Anole.getProperty("anole.client.subscriber.password", "123")); 
    	authBody.setClientType(ClientType.SUBSCRIBER);
    	authBody.setEnvironment(Anole.getCurrentEnvironment());
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
		      	AnoleAuthenticationClient.authenticating = false;
		      	ReferenceCountUtil.release(msg);
			} break;
		 	case S2C_AUTH_PASS:{ 
		 		logger.info ("[:)] Authentication passed."); 
		 		AssignedWorkerInfoMessage authenResult = ((AssignedWorkerInfoMessage)msg);
		 		if(authenResult.getPort() <=0 || "null".equals(authenResult.getIp())){
		 			logger.error("[:(] Can not to connect to worker server. More details: {}", authenResult.getMessage());
		 			break;
		 		}
		 		logger.info ("[:)] Successfully connectted to the worker server {}:{}.", authenResult.getIp(), authenResult.getPort()); 
		 		anoleSubscriberClient.setWorkerServer(
		 				authenResult.getIp(),
		 				authenResult.getPort(),
		 				authenResult.getClientId(), 
		 				authenResult.getToken()); 	
		 		AnoleAuthenticationClient.authenticating = false;
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
