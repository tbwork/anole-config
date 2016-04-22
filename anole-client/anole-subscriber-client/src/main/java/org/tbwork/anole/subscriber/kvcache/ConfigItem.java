package org.tbwork.anole.subscriber.kvcache;

import java.math.BigDecimal;

import javax.xml.transform.TransformerConfigurationException;

import org.tbwork.anole.common.ConfigValueType;
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
	private ConfigValueType type;
	
	@Setter(AccessLevel.NONE)
	private String strValue; 
	@Setter(AccessLevel.NONE)
	private boolean boolValue;
	@Setter(AccessLevel.NONE)
	private double doubleValue;
	@Setter(AccessLevel.NONE)
	private float floatValue;
	@Setter(AccessLevel.NONE)
	private int intValue;
	@Setter(AccessLevel.NONE)
	private long longValue;
	@Setter(AccessLevel.NONE)
	private short shortValue; 

	public void setValue(String value, ConfigValueType type)
	{
		this.type = type;
		value = value.trim();
		switch(type){
			case BOOL:{ //set boolValue
				if(!"true".equals(value) && !"false".equals(value)) 
					throw new BadTransformValueFormatException(value, ConfigValueType.BOOL); 
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
					 throw new BadTransformValueFormatException(value, ConfigValueType.NUMBER); 
				 }  
			}break;
			case STRING:{
				 strValue = value;
			}break;
			default:break;
		}
	}
	
	public String getStrValue(){
	    return strValue;	
	}
	
	public int intValue()
	{
		if(ConfigValueType.NUMBER.equals(type))
		{
			return intValue;
		}
		return 0;
	}
	
	public boolean boolValue()
	{
		if(ConfigValueType.NUMBER.equals(type))
		{
			return boolValue;
		}
		return false;
	}
	
	public double doubleValue()
	{
		if(ConfigValueType.NUMBER.equals(type))
		{
			return doubleValue;
		}
		return 0;
	}
	
	public float floatValue()
	{
		if(ConfigValueType.NUMBER.equals(type))
		{
			return floatValue;
		}
		return 0;
	}
	
	public short shortValue()
	{
		if(ConfigValueType.NUMBER.equals(type))
		{
			return shortValue;
		}
		return 0;
	}
	
	
	
	private <T> T getObject(Class<T> clazz)
	{
		if(ConfigValueType.JSON.equals(type)) 
			return JSON.parseObject(strValue, clazz); 
		else
			throw new IllegalConfigTransformException(type, ConfigValueType.JSON);
	} 
	 
	
	public static void main(String[] args) {
		
		String value = "-1.0";
		
		BigDecimal a = new BigDecimal(value);
		System.out.println(a);
		
	}
}
