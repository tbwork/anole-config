package org.tbwork.anole.publisher.client.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tbwork.anole.common.UnixTime;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.CommonAuthenticationMessage;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
import org.tbwork.anole.loader.core.Anole;
import org.tbwork.anole.publisher.client.IAnolePublisherClient;
import org.tbwork.anole.publisher.client.StaticClientConfig;
import org.tbwork.anole.publisher.client.impl.AnolePublisherClient; 

public class AuthenticationHandler extends  SimpleChannelInboundHandler<Message>  {

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

	private IAnolePublisherClient anolePublisher = AnolePublisherClient.instance();
	
	public AuthenticationHandler(){
		super(false);
	} 
	private CommonAuthenticationMessage getAuthInfo(){
		CommonAuthenticationMessage authBody=new CommonAuthenticationMessage();
		String username = Anole.getProperty("anole.client.publisher.username", "tangbo");
		String password = Anole.getProperty("anole.client.publisher.password", "123");
    	authBody.setUsername(username);
    	authBody.setPassword(password); 
    	authBody.setClientType(ClientType.PUBLISHER);
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
	        	AuthFailAndCloseMessage amsg = (AuthFailAndCloseMessage) msg;
		      	logger.error("[:(] Authen failed ({}), please check them and try again.", amsg.getNote()); 
		      	anolePublisher.close(); // close the connection and wait for next trial
		      	ReferenceCountUtil.release(msg);
			} break;
		 	case S2C_AUTH_PASS:{ 
		 		logger.info ("[:)] Login successfully."); 
		 		anolePublisher.saveToken(((AuthPassWithTokenMessage)msg).getClientId(), ((AuthPassWithTokenMessage)msg).getToken()); 
		 		anolePublisher.notifyConnectOver(true);
		 		ReferenceCountUtil.release(msg); 
		 	} break;
		 	case S2C_MATCH_FAIL:{
		 		logger.error("[:(] Connection is disabled by the server or becasuse of the network problem, automatically connect immediately.");
		 		anolePublisher.close();
		 		TimeUnit.SECONDS.sleep(StaticClientConfig.RECONNECT_INTERVAL);
		 		anolePublisher.connect();
		 	} break;
	        default:{ 
	        } break;
        }
        // pass to next
        ctx.fireChannelRead(msg); 
		
	}
    
}
