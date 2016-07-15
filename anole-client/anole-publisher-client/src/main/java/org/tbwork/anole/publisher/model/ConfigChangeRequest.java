package org.tbwork.anole.publisher.model;

import org.tbwork.anole.common.model.ConfigChangeDTO;

import lombok.Data;
 
@Data
public class ConfigChangeRequest {

	private String operator;
	private ConfigChangeDTO configChangeDTO;
	
}
