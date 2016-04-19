package org.anole.hub;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class DiscardServerHandler extends ChannelHandlerAdapter{

	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { 
        // Discard the received data silently.
        // ((ByteBuf) msg).release(); 
		//Echo server
		ctx.write(msg); 
	    ctx.flush(); 
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { 
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
	
}
