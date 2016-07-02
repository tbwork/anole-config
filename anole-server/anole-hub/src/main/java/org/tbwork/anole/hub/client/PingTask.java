package org.tbwork.anole.hub.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tbwork.anole.common.message.c_2_s.PingMessage; 


public class PingTask extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(PingTask.class);
	@Autowired
	private AnoleClient client;
	
	@Override
	public void run() { 
		try{ 
			 ping(); 
		}
		catch(Exception e){
			logger.error("Ping failed. Details: {}", e.getMessage());
		} 
	}


	private void ping(){   
	    client.connect();
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
