package org.tbwork.anole.gui.domain.config;

import java.util.List;

import org.tbwork.anole.gui.domain.model.ConfigInfo;
import org.tbwork.anole.gui.domain.model.ConfigBrief; 
import org.tbwork.anole.gui.domain.model.demand.AddConfigDemand;
import org.tbwork.anole.gui.domain.model.demand.DeleteConfigDemand; 
import org.tbwork.anole.gui.domain.model.demand.GetConfigByKeyAndEnvDemand;
import org.tbwork.anole.gui.domain.model.demand.GetConfigsByProjectAndEnvDemand;
import org.tbwork.anole.gui.domain.model.demand.ModifyConfigDemand; 
import org.tbwork.anole.gui.domain.model.result.DeleteConfigResult;
import org.tbwork.anole.gui.domain.model.result.ModifyConfigResult;

public interface IConfigService {

	List<ConfigBrief> getConfigsByProjectAndEnv(GetConfigsByProjectAndEnvDemand demand);
	
	ConfigBrief addConfig(AddConfigDemand config);
	
	ConfigBrief modifyConfig(ModifyConfigDemand config);
	
	ConfigBrief getConfigByKeyAndEnv(String key,String env);
	
	DeleteConfigResult deleteConfig(DeleteConfigDemand demand);
	
	ConfigInfo getConfigInfoByKeyCacheable(String key);
}
