package org.tbwork.anole.hub.server.boss._4_worker.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tbwork.anole.common.message.s_2_c.AuthFailAndCloseMessage;
import org.tbwork.anole.common.message.s_2_c.AuthenticationFirstMessage;
import org.tbwork.anole.hub.server.util.ChannelHelper;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
@Component("b4wNewConnectionHandler")
@Sharable
public class NewConnectionHandler extends ChannelHandlerAdapter {

	static final Logger logger = LoggerFactory.getLogger(NewConnectionHandler.class);
	
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        if(logger.isDebugEnabled())
        	logger.debug("[:)] A new connection is established, remote address : '{}'", ctx.channel().remoteAddress());
        AuthenticationFirstMessage afMsg = new AuthenticationFirstMessage();
        ChannelHelper.sendMessageSync(ctx, afMsg); 
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}