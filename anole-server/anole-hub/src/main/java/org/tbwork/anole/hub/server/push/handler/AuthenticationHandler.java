package org.tbwork.anole.hub.server.push.handler;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.AuthenticationBodyMessage;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthPassWithTokenMessage;
import org.tbwork.anole.common.message.s_2_c.MatchFailAndCloseMessage;
import org.tbwork.anole.hub.server.client.manager.BaseClientManager;
import org.tbwork.anole.hub.server.client.manager.impl.SubscriberClientManager;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberRegisterRequest;
import org.tbwork.anole.hub.server.client.manager.model.SubscriberValidateRequest;
import org.tbwork.anole.hub.server.push.AnolePushServer;
import org.tbwork.anole.hub.server.util.ChannelHelper;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator;
import org.tbwork.anole.hub.server.util.ClientInfoGenerator.ClientInfo; 

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

@Component()
public class AuthenticationHandler extends SimpleChannelInboundHandler<Message> {

	@Autowired
	@Qualifier("subscriberClientManager")
	private SubscriberClientManager cm;

	static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	public AuthenticationHandler(){
		super(false);
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    } 

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message msg)
			throws Exception { 
    	 Message message = msg;
    	 MessageType msgType = message.getType(); 
		 if(MessageType.C2S_AUTH_BODY.equals(msgType)) // authentification information from client
		 {
			    AuthenticationBodyMessage bodyMessage = (AuthenticationBodyMessage) msg;
		 		// read from database
		 		if( "tommy.tang".equals(bodyMessage.getUsername()) && 
		 				"123456".equals(bodyMessage.getPassword()))
		 		{    
		 			  // send back the clientId and the access token
		 			  AuthPassWithTokenMessage aptMsg = new AuthPassWithTokenMessage();
		 			  ClientInfo clientInfo =  ClientInfoGenerator.generate(); 
		 			  aptMsg.setClientId(clientInfo.getClientId());
		 			  aptMsg.setToken(clientInfo.getToken());
		 			  ctx.channel().writeAndFlush(aptMsg);  
		 			  //Register client 
		 			  cm.registerClient(new SubscriberRegisterRequest(clientInfo.getClientId(), 
		 					  										  clientInfo.getToken(), 
		 					  										  (SocketChannel)ctx.channel()
		 					  										  ));
		 			  if(logger.isDebugEnabled())
		 				  logger.info("[:)] New user logined successfully! username:'{}'", "tommy.tang");
		 			  ReferenceCountUtil.release(msg);// Releasing the message which means no further process.
		 		}
		 		else  
		 		{   
		 			// invalid connection trial, send AuthFailAndCloseMessage message and then close the connection immediately.
		 			AuthFailAndCloseMessage afcMsg = new AuthFailAndCloseMessage(); 
		 			ChannelHelper.sendAndClose(ctx, afcMsg);
		 		}
		 }
		 else 
		 { // other message must need be validated (identification) before further process.
			   if(!cm.validte(new SubscriberValidateRequest(message.getClientId(), message.getToken())))
			   {
				   MatchFailAndCloseMessage mfcMsg = new MatchFailAndCloseMessage(); 
				   ChannelHelper.sendAndClose(ctx, mfcMsg);
			   }
		 }
		
		 // Passed the identification validation, go on processing logical staff.
         ctx.fireChannelRead(msg); 
	}
 
}