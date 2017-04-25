package org.tbwork.anole.gui.domain.model.demand;

import com.google.common.base.Preconditions;

import lombok.Data;

@Data
public class GetConfigByKeyAndEnvDemand {

	private String key;
	private String env;
	
	public void preCheck(){
		Preconditions.checkArgument(key!=null && !key.isEmpty(), "key should not be null or empty!");
		Preconditions.checkArgument(env!=null && !env.isEmpty(), "env should not be null or empty!");
	}
}
