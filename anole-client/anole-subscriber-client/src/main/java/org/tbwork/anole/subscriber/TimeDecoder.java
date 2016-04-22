package org.tbwork.anole.subscriber;

import java.util.List;

import org.tbwork.anole.common.UnixTime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TimeDecoder extends ByteToMessageDecoder { 
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { 
    	 if (in.readableBytes() < 4) {
    	        return;
    	    }

	    out.add(new UnixTime(in.readUnsignedInt()));
    }
}