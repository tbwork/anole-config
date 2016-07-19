package org.tbwork.anole.publisher.model;

import org.tbwork.anole.common.model.ConfigModifyDTO;

import lombok.Data;
 
@Data
public class ConfigChangeRequest {

	private String operator;
	private ConfigModifyDTO configChangeDTO;
	
}
