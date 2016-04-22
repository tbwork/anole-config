package org.tbwork.anole.subscriber;

import java.util.Date;

import org.tbwork.anole.common.UnixTime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler  extends ChannelHandlerAdapter{

	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) {
	    	   UnixTime m = (UnixTime) msg;
    	       System.out.println(m);
    	       ctx.close();
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        cause.printStackTrace();
	        ctx.close();
	    }
	
}
