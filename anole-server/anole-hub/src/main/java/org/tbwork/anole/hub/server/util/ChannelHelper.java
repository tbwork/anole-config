package org.tbwork.anole.hub.server.util;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ChannelHelper {

	public static void sendAndClose(final ChannelHandlerContext ctx, Object msg)
	{ 
			final ChannelFuture f = ctx.writeAndFlush(msg);  
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) {
	                assert f == future;
	                ctx.close();
	            }
			}); 
	}
	
}
