package org.tbwork.anole.hub.server.push.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NewConnectionHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        //发现新的链接，立即要求其提供身份信息
    	System.out.println("链接已经建立");
    	ctx.fireChannelActive();
    }

    
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}