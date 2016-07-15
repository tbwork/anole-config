package org.tbwork.anole.common;

public enum ConfigType {

	NUMBER((byte)1),
	BOOL((byte)2),
	STRING((byte)3),
	JSON((byte)4);
	
	private Byte code;
	
	private ConfigType(Byte code){
		this.code = code;
	}
	
	public static ConfigType configType(Byte index){
		for(ConfigType item : ConfigType.values())
			if(item.code == index) 
				return item;
		return STRING; //DEFAULT
	} 
	
	public Byte index(){
		return code;
	}
}
