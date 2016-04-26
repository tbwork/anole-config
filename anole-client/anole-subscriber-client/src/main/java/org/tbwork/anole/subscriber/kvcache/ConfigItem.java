package org.tbwork.anole.subscriber.kvcache;

import java.math.BigDecimal;

import javax.xml.transform.TransformerConfigurationException;

import org.tbwork.anole.common.ConfigType;  
import org.tbwork.anole.subscriber.exceptions.BadTransformValueFormatException;
import org.tbwork.anole.subscriber.exceptions.IllegalConfigTransformException;

import com.alibaba.fastjson.JSON;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ConfigItem {

	private String key;
	private ConfigType type;
	
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private String strValue; 
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private boolean boolValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private double doubleValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private float floatValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private int intValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private long longValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private short shortValue; 
	/**
	 * True empty means user does not set value for the configuration.
	 */
	private boolean empty; 
	
	private boolean loaded;
	
	private ConfigItem(){
		this.key = new String();
		setSystemDefault();
		
	}
	
	public ConfigItem(String key){
		this.key = key;
		setSystemDefault();
	}
	
	public void setValue(String value, ConfigType type)
	{
		this.type = type;
		value = value.trim();
		if(value == null || value.isEmpty())
			return;
		empty = false;
		loaded = true;
		switch(type){
			case BOOL:{ //set boolValue
				if(!"true".equals(value) && !"false".equals(value)) 
					throw new BadTransformValueFormatException(value, ConfigType.BOOL); 
				if("true".equals(value)) 
					boolValue = true;
				else
					boolValue = false; 
			}break;
			case JSON: {// set strValue
				 strValue = value; 
			}break;
			case NUMBER:{// set intValue shortValue longValue floatValue doubleValue 
				 try{
					 BigDecimal a = new BigDecimal(value);
					 intValue = a.toBigInteger().intValue();
					 shortValue = (short) intValue;
					 longValue = a.toBigInteger().longValue();
					 floatValue = a.floatValue();
					 doubleValue = a.doubleValue();
				 }
				 catch(NumberFormatException e)
				 {
					 throw new BadTransformValueFormatException(value, ConfigType.NUMBER); 
				 }  
			}break;
			case STRING:{
				 strValue = value;
			}break;
			default:break;
		}
	}
	
	public String strValue(){
	    return strValue;	
	}
	
	public int intValue()
	{
		if(ConfigType.NUMBER.equals(type))
		{
			return intValue;
		}
		return 0;
	}
	
	public boolean boolValue()
	{
		if(ConfigType.BOOL.equals(type))
		{
			return boolValue;
		}
		return false;
	}
	
	public double doubleValue()
	{
		if(ConfigType.NUMBER.equals(type))
		{
			return doubleValue;
		}
		return 0;
	}
	
	public float floatValue()
	{
		if(ConfigType.NUMBER.equals(type))
		{
			return floatValue;
		}
		return 0;
	}
	
	public short shortValue()
	{
		if(ConfigType.NUMBER.equals(type))
		{
			return shortValue;
		}
		return 0;
	}
	
	public long longValue()
	{
		if(ConfigType.NUMBER.equals(type))
		{
			return longValue;
		}
		return 0;
	}
	
	
	
	public <T> T objectValue(Class<T> clazz)
	{
		if(ConfigType.JSON.equals(type)) 
			return JSON.parseObject(strValue, clazz); 
		else
			throw new IllegalConfigTransformException(type, ConfigType.JSON);
	} 
	
	private void setSystemDefault(){
		this.empty       = true;
		this.boolValue   = false;
		this.doubleValue = 0.0;
		this.floatValue  = 0.0f;
		this.intValue    = 0;
		this.longValue   = 0l;
		this.shortValue  = 0;
		this.strValue    = null;
	}
	 
	
}
