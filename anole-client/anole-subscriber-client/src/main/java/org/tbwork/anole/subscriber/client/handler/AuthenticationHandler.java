package org.tbwork.anole.subscriber.client.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tbwork.anole.common.UnixTime;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.AuthenticationBodyMessage;
import org.tbwork.anole.subscriber.client.AnoleSubscriberClient;

public class AuthenticationHandler extends  ChannelHandlerAdapter  {

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	@Autowired
	private AnoleSubscriberClient anoleSubscriberClient;
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		  Message message = (Message) msg;
		  MessageType msgType=  message.getType();
          switch (msgType){
            case S2C_AUTH_FIRST:{ //Please login first. 
            	ctx.writeAndFlush(getAuthInfo());
            	ReferenceCountUtil.release(msgType);
            } break;
            case S2C_AUTH_FAIL_CLOSE:{
            	logger.error("[:(] Username or password is invalid, please check them and try again."); 
            	anoleSubscriberClient.close(); // close the connection and wait for next trial
            	ReferenceCountUtil.release(msgType);
			} break;
		 	case S2C_AUTH_PASS:{ 
		 		logger.info ("[:)] Login successfully.");
		 		anoleSubscriberClient.setClientId(message.getClientId());
		 		anoleSubscriberClient.setToken(message.getToken());
		 		ReferenceCountUtil.release(msgType);
		 	} break;
		 	case S2C_MATCH_FAIL:{
		 		logger.error("[:(] Connection is disabled by the server or becasuse of the network problem, automatically connect immediately.");
		 		ctx.writeAndFlush(getAuthInfo());
		 		ReferenceCountUtil.release(msgType);
		 	} break;
            default:{
					 ctx.fireChannelRead(msg);
	        } break;
          }
          
	}
	
	
	private AuthenticationBodyMessage getAuthInfo(){
		AuthenticationBodyMessage authBody=new AuthenticationBodyMessage();
    	authBody.setUsername("tommy.tang");
    	authBody.setPassword("123456"); 
    	return authBody;
	}
    
}
