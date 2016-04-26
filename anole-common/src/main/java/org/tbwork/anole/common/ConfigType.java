package org.tbwork.anole.common;

public enum ConfigType {

	NUMBER(1),
	STRING(2),
	BOOL(3),
	JSON(4);
	
	private int code;
	
	private ConfigType(int code){
		this.code = code;
	}
	
	public static ConfigType configType(int index){
		for(ConfigType item : ConfigType.values())
			if(item.code == index) 
				return item;
		return STRING; //DEFAULT
	}
}
