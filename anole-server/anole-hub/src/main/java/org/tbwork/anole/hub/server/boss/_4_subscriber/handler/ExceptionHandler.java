package org.tbwork.anole.hub.server.boss._4_subscriber.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthenticationFirstMessage;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
/**
 * Deal with all exceptions.
 * @author tommy.tang
 */
@Component("b4sExceptionHandler")
@Sharable
public class ExceptionHandler extends ChannelHandlerAdapter {

	static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class); 
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
    	if(cause instanceof IOException) {
    		logger.warn("The connection with subscriber (address = {}) disconnected initiatively! ", ctx.channel().remoteAddress());
    	}
    	else {
    		cause.printStackTrace();
    	} 
        ctx.close();
    }
}