package org.tbwork.anole.hub.server.lccmanager.impl;

import io.netty.channel.socket.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tbwork.anole.common.ConfigType;
import org.tbwork.anole.common.enums.ClientType;
import org.tbwork.anole.common.model.ConfigModifyResultDTO;
import org.tbwork.anole.common.model.ConfigModifyDTO;
import org.tbwork.anole.hub.server.lccmanager.model.clients.LongConnectionClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.PublisherClient;
import org.tbwork.anole.hub.server.lccmanager.model.clients.WorkerClient;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterParameter;
import org.tbwork.anole.hub.server.lccmanager.model.requests.RegisterRequest;
import org.tbwork.anole.hub.server.util.ClientEntropyUtil;

import com.google.common.base.Preconditions; 

/**
 * Publisher manager used by boss server.
 * @author tommy.tang
 */
@Service("publisherClientManager")
public class PublisherClientManagerForBoss  extends LongConnectionClientManager {

	private static final Logger logger = LoggerFactory.getLogger(PublisherClientManagerForBoss.class); 
 
	
	public static enum Operation{
		VIEW(1),   // never used in publisher.
		MODIFY(2), // Modifying a configuration needs at least admin right
		CREATE(2), // Creating a configuration needs at least admin right
		DELETE(3); // Deleting a configuration needs at least owner right
		private int level;
		private Operation(int level){
			this.level = level;
		}
	}
	
	@Override
	protected LongConnectionClient createClient(int token, RegisterRequest registerRequest) { 
		return new PublisherClient(token, registerRequest.getSocketChannel());
	} 
	
	
 
	public ConfigModifyResultDTO motifyConfig(String operator, ConfigModifyDTO ccd){
		ConfigModifyResultDTO result  = new ConfigModifyResultDTO();
		Preconditions.checkArgument(operator!=null && !operator.isEmpty(), "The operator should not be null or empty.");
		Preconditions.checkNotNull(ccd, "Config change content should not be null.");
		Preconditions.checkArgument(ccd.getProject()!=null && !ccd.getProject().isEmpty(), "A project should be specified before you changing its configurations.");		
		try{
			if(ccd.getOriConfigType() == null){ //new configuration
				createConfig(operator, ccd.getProject(), ccd.getKey(), ccd.getDestValue(), ccd.getDestConfigType());
			}
			else{
				updateConfig(operator, ccd.getProject(), ccd.getKey(), ccd.getEnv(), ccd.getOrigValue(), ccd.getDestValue(), ccd.getOriConfigType(), ccd.getDestConfigType());
			}
			result.setErrorMsg("OK");
			result.setSuccess(true);
		}
		catch(Exception e){
			logger.error("Add config failed, details: {}", e.getMessage());
			result.setSuccess(false);
			result.setErrorMsg("Add config failed, details: " + e.getMessage());
		}
		return result;
	} 
	
	private boolean validateRight(String operator, String project, Operation operation){
		//TODO
		return false;
	}
	
	private void updateConfig(String operator, String project, String key, String env, String oldValue, String newValue, ConfigType oldConfigType, ConfigType newConfigType){
		Preconditions.checkArgument(key!=null && !key.isEmpty(), "Key must be specified!");
		Preconditions.checkArgument(env!=null && !env.isEmpty(), "Env must be specified!");
		Preconditions.checkArgument(project!=null && !project.isEmpty(), "Project must be specified!");
		Preconditions.checkNotNull(oldConfigType,"Old config type must be specified!");
		Preconditions.checkNotNull(newConfigType,"New config type must be specified!");
		Preconditions.checkArgument(validateRight(operator, project, Operation.MODIFY), "The operator ("+operator+") has no right to this operation.");
		//
	}
	
	private void createConfig(String operator, String project, String key, String value,  ConfigType newConfigType){
		Preconditions.checkArgument(key!=null && !key.isEmpty(), "Key must be specified!");
		Preconditions.checkNotNull(newConfigType,"Config type must be specified!");
		Preconditions.checkArgument(project!=null && !project.isEmpty(), "Project must be specified!");
		Preconditions.checkArgument(validateRight(operator, project, Operation.CREATE), "The operator ("+operator+") has no right to this operation.");
		
	}
}
