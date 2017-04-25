package org.tbwork.anole.subscriber.client._2_worker.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbwork.anole.common.message.Message;
import org.tbwork.anole.common.message.MessageType;
import org.tbwork.anole.common.message.c_2_s.subscriber._2_worker.C2WChangeNotifyAckMessage;
import org.tbwork.anole.common.message.s_2_c.worker._2_subscriber.W2SConfigChangeNotifyMessage;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.common.model.ValueChangeDTO;
import org.tbwork.anole.subscriber.client._2_worker.IAnoleSubscriberClient;
import org.tbwork.anole.subscriber.client._2_worker.impl.AnoleSubscriberClient;
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
	
	private IAnoleSubscriberClient anoleSubscriberClient = AnoleSubscriberClient.instance();
	
	private ConfigChangeNotifyMessageHandler(){
		super(MessageType.S2C_CONFIG_CHANGE_NOTIFY_W_2_C);
	}
	
	public static ConfigChangeNotifyMessageHandler instance(){
		return configChangeNotifyMessageHandler;
	}

	@Override
	public void process(Message message) {
		W2SConfigChangeNotifyMessage ccnMsg = (W2SConfigChangeNotifyMessage) message;
		Preconditions.checkNotNull(ccnMsg.getValueChangeDTO(), "Config change details should not be null.");
		Preconditions.checkNotNull(ccnMsg.getValueChangeDTO().getKey(),"Config key should not be null.");
		Preconditions.checkArgument(!ccnMsg.getValueChangeDTO().getKey().isEmpty(), "Config key should not be empty.");
		
		ValueChangeDTO vcd = ccnMsg.getValueChangeDTO();
		// Pre Observers
		boolean stopAfterProcess = preProcess(vcd);
		if(stopAfterProcess)
			return ;
		// Refresh local configuration.
		cm.setConfigItem(ccnMsg.getValueChangeDTO().getKey(), 
									ccnMsg.getValueChangeDTO().getValue(), 
									ccnMsg.getValueChangeDTO().getConfigType()); 
		// Post Observers
		postProcess(vcd);
		
		//ack process of key
		C2WChangeNotifyAckMessage c2WChangeNotifyAckMessage = new C2WChangeNotifyAckMessage();
		c2WChangeNotifyAckMessage.setKey(vcd.getKey());
		c2WChangeNotifyAckMessage.setTimestamp(vcd.getTimestamp());
		anoleSubscriberClient.sendMessage(c2WChangeNotifyAckMessage);
	}
 
	
	private boolean preProcess(ValueChangeDTO vcd){
		if(om.getPreObservers(vcd.getKey()) == null)
			return false;
		for(ConfigObserver item : om.getPreObservers(vcd.getKey())){
			 item.process(vcd);
			 if(item.stopAfterProcess())  
				 return true;
		}
		return false;
	}
	
	private void postProcess(ValueChangeDTO vcd){
		if(om.getPostObservers(vcd.getKey()) == null)
			return;
		for(ConfigObserver item : om.getPostObservers(vcd.getKey())){
			 item.process(vcd); 
			 if(item.stopAfterProcess())
				 return;
		}
	}
	
	
	
}
