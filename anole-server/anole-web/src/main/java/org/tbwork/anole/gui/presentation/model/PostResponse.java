package org.tbwork.anole.gui.presentation.model;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class PostResponse<T> { 
	private boolean success;
	private String errorMessage;
	private T result;
	private long costTime; 
	private boolean loginStatus; 
	
	public String toStringForReturn(boolean loginStatus){
		this.loginStatus = loginStatus;
		return JSON.toJSONString(this);
	}
}
