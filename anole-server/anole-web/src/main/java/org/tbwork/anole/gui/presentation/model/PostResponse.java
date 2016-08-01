package org.tbwork.anole.gui.presentation.model;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class PostResponse<T> { 
	private boolean success;
	private String errorMessage;
	private T result;
	
	public String toString(){
		return JSON.toJSONString(this);
	}
}
