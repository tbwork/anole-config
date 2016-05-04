package org.tbwork.anole.subscriber.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.c_2_s.PingMessage;

public class PingTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(PingTask.class);
	private AnoleSubscriberClient client = AnoleSubscriberClient.instance();
	
	@Override
	public void run() { 
		 ping();  
	}


	private void ping(){  
		if(logger.isInfoEnabled()){
			client.sendMessageWithListeners(new PingMessage(), 
				new ChannelFutureListener(){

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						logger.info("[:)] Ping message is sent successfully.");
					}
		
				}
			);
		}
		else
			client.sendMessage(new PingMessage());
		
	}
}
