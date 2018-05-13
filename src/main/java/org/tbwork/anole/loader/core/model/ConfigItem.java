package org.tbwork.anole.loader.core.model;

import java.math.BigDecimal;
import org.tbwork.anole.loader.exceptions.BadTransformValueFormatException;
import org.tbwork.anole.loader.exceptions.ConfigTypeNotMatchedException;
import org.tbwork.anole.loader.types.ConfigType;
import org.tbwork.anole.loader.util.StringUtil;

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
	private Boolean boolValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private Double doubleValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private Float floatValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private Integer intValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private Long longValue;
	@Getter(AccessLevel.NONE)@Setter(AccessLevel.NONE)
	private Short shortValue; 

	/**
	 * True empty means user does not set value 
	 * (or set an empty value) for the configuration.
	 */
	private boolean empty; 
	
	/**
	 * Indicates that at least the setValue() was called 
	 * once after it was created.
	 */
	private volatile boolean loaded; 
	private boolean atFinal;
	
	/**
	 * In case of that the key is always in wait status,
	 *  and no value-setter any more.
	 */
	private volatile boolean giveup;
	
	private ConfigItem(){
		this.key = new String();
		setSystemDefault();
		
	}
	
	public ConfigItem(String key){
		this.key = key;
		setSystemDefault();
	}
	
	public ConfigItem(String key , String value, ConfigType type){
		this.key = key;
		this.setValue(value, type);
	}
	
	public void setValue(String value, ConfigType type)
	{
		synchronized(key){  
			try{
				this.type = type;
				loaded = true;
				if(value == null)
					return; 
				value = value.trim();
				this.strValue = value;
				checkFinal();
				if(!atFinal)
					return; 
				empty = false;
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
						 if(value.isEmpty())
							 throw new BadTransformValueFormatException(value, ConfigType.JSON); 
						 strValue = value; 
					}break;
					case NUMBER:{// set intValue shortValue longValue floatValue doubleValue 
						 if(value.isEmpty())
							 throw new BadTransformValueFormatException(value, ConfigType.NUMBER); 
						 try{
							 BigDecimal a = new BigDecimal(value);
							 intValue = a.toBigInteger().intValue();
							 shortValue = intValue.shortValue();
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
						 try{
							 BigDecimal a = new BigDecimal(value);
							 intValue = a.toBigInteger().intValue();
							 shortValue = intValue.shortValue();
							 longValue = a.toBigInteger().longValue();
							 floatValue = a.floatValue();
							 doubleValue = a.doubleValue();
						 }
						 catch(NumberFormatException e)
						 {
							//do nothing
						 }  
					}break;
					default:break;
				} 
			}
			finally{
				key.notifyAll();   
			} 
		} 
	}
	
	public String strValue(){
	    return strValue;	
	}
	
	public int intValue()
	{
		if(intValue != null)
		{
			return intValue;
		} 
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	public boolean boolValue()
	{
		if(boolValue != null)
		{
			return boolValue;
		}
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	public double doubleValue()
	{
		if(doubleValue != null)
		{
			return doubleValue;
		}
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	public float floatValue()
	{
		if(floatValue != null)
		{
			return floatValue;
		}
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	public short shortValue()
	{
		if(shortValue != null)
		{
			return shortValue;
		}
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	public long longValue()
	{
		if(longValue != null)
		{
			return longValue;
		}
		throw new ConfigTypeNotMatchedException(type, ConfigType.NUMBER);
	}
	
	
	private void checkFinal(){ 
		this.atFinal = !StringUtil.checkContainVariable(this.strValue, this.key);
	}
	
	
	/**
	 * If you want to get POJO object from the configuration
	 * value, make sure the value is a valid JSON string.
	 * @param clazz the POJO's class
	 * @return the object of POJO's class
	 */
	public <T> T objectValue(Class<T> clazz)
	{
		if(ConfigType.JSON.equals(type)) 
			return JSON.parseObject(strValue, clazz); 
		else
			throw new ConfigTypeNotMatchedException(type, ConfigType.JSON);
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
		this.atFinal = true;
	}
	 
	
}
