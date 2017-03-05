package org.tbwork.anole.subscriber.client._2_worker.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2CConfigChangeNotifyMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO; 
import org.tbwork.anole.subscriber.core.ConfigObserver;
import org.tbwork.anole.subscriber.core.ObserverManager;
import org.tbwork.anole.subscriber.core.impl.SubscriberConfigManager;

import com.google.common.base.Preconditions;

import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ConfigChangeNotifyMessageHandler extends SpecifiedMessageHandler{

	private static final ConfigChangeNotifyMessageHandler configChangeNotifyMessageHandler = new ConfigChangeNotifyMessageHandler();
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigChangeNotifyMessageHandler.class);
	
	private ObserverManager om = ObserverManager.instance();  
	
	private SubscriberConfigManager cm = SubscriberConfigManager.getInstance();
	
	private ConfigChangeNotifyMessageHandler(){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_W_2_C);
	}
	
	public static ConfigChangeNotifyMessageHandler instance(){
		return configChangeNotifyMessageHandler;
	}

	@Override
	public void process(Message message) {
		W2CConfigChangeNotifyMessage ccnMsg = (W2CConfigChangeNotifyMessage) message;
		Preconditions.checkNotNull(ccnMsg.getConfigChangeDTO(), "Config change details should not be null.");
		Preconditions.checkNotNull(ccnMsg.getConfigChangeDTO().getKey(),"Config key should not be null.");
		Preconditions.checkArgument(!ccnMsg.getConfigChangeDTO().getKey().isEmpty(), "Config key should not be empty.");
		
		ConfigModifyDTO ccDto = ccnMsg.getConfigChangeDTO();
		// Pre Observers
		boolean stopAfterProcess = preProcess(ccDto);
		if(stopAfterProcess)
			return ;
		// Refresh local configuration.
		cm.setConfigItem(ccnMsg.getConfigChangeDTO().getKey(), 
									ccnMsg.getConfigChangeDTO().getValue(), 
									ccnMsg.getConfigChangeDTO().getConfigType()); 
		// Post Observers
		postProcess(ccDto);
	}
 
	
	private boolean preProcess(ConfigModifyDTO ccDto){
		for(ConfigObserver item : om.getPreObservers(ccDto.getKey())){
			 item.process(ccDto);
			 if(item.stopAfterProcess())  
				 return true;
		}
		return false;
	}
	
	private void postProcess(ConfigModifyDTO ccDto){
		for(ConfigObserver item : om.getPostObservers(ccDto.getKey())){
			 item.process(ccDto); 
			 if(item.stopAfterProcess())
				 return;
		}
	}
	
	
	
}
