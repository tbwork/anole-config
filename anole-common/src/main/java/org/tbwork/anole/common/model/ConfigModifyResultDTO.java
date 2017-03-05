package org.tbwork.anole.common.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConfigModifyResultDTO implements Serializable{
	private boolean success;
	private String errorMsg;
	
}
