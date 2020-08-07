package org.tbwork.anole.loader.core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.register.converter.impl.BooleanConverter;
import org.tbwork.anole.loader.core.register.converter.impl.DigitalConverter;
import org.tbwork.anole.loader.exceptions.ConfigTypeNotMatchedException;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.tbwork.anole.loader.util.AnoleValueUtil;

public class ConfigItem {

	private String key;

	/**
	 * Definition of the config, like " ${switch} ? ${small.value} : ${big.value}".
	 */
	private String definition;


	/**
	 * <p>Non-empty indicates something wrong occurred in calculating the value,
	 * and this field is the reason note.</p>
	 * Only works in not strict mode, coz in strict mode, exceptions would be
	 * thrown out and request thread would be terminated.
	 */
	private String error;


	/**
	 * different data type of values
	 */
	private String strValue;
	private Boolean boolValue;
	private Double doubleValue;
	private Float floatValue;
	private Integer intValue;
	private Long longValue;
	private Short shortValue; 

	/**
	 * Last update timestamp.
	 */
	private volatile long lastUpdateTime;

	/**
	 * Keys of configs which are referencing the current config key.
	 */
	private Set<String> parentConfigKeys;

	/**
	 * Keys of configs which are referenced by the current config key.
	 */
	private Set<String> childConfigKeys;
	
	public ConfigItem(String key){
		this.key = key.trim();
		parentConfigKeys = new HashSet<>();
		childConfigKeys = new HashSet<>();
		setSystemDefault();
	}




	/**
	 * After the value is calculated, value should not contain any variable
	 * or any expression.
	 * @param value the concrete value of the config
	 */
	public void setValue(String value) {
		if(value == null)
			return;
		value = value.trim();
		this.strValue = value;
		this.error = null;
		if(!Anole.initialized){
			//Add to JVM system properties for other frameworks to read.
			//System.setProperty(key, strValue);
		}
		else{
			// only for those configs who were already existed in system properties.
			if(System.getProperty(key) != null && !System.getProperty(key).equals(value)){
				System.setProperty(key, strValue);
			}
		}
		BigDecimal bigDecimal =new DigitalConverter().convert(strValue);
		if(bigDecimal != null){
			this.doubleValue = bigDecimal.doubleValue();
			this.floatValue = bigDecimal.floatValue();
			this.intValue = bigDecimal.intValue();
			this.longValue = bigDecimal.longValue();
			this.shortValue = bigDecimal.shortValue();
		}
		else{
			this.doubleValue = null;
			this.floatValue = null;
			this.intValue = null;
			this.longValue = null;
			this.shortValue = null;
		}

		this.boolValue = new BooleanConverter().convert(strValue);
		this.lastUpdateTime = System.nanoTime();
	}
	
	public String strValue(){
	    return strValue;	
	}
	
	public int intValue()
	{
		if( intValue != null)
		{
			return intValue;
		} 
		throw new ConfigTypeNotMatchedException(strValue, "int/Integer");
	}
	
	public boolean boolValue()
	{
		if(boolValue != null)
		{
			return boolValue;
		}
		throw new ConfigTypeNotMatchedException(strValue, "boolean/Boolean");
	}
	
	public double doubleValue()
	{
		if(doubleValue != null)
		{
			return doubleValue;
		}
		throw new ConfigTypeNotMatchedException(strValue, "double/Double");
	}
	
	public float floatValue()
	{
		if(floatValue != null)
		{
			return floatValue;
		}
		throw new ConfigTypeNotMatchedException(strValue, "float/Float");
	}
	
	public short shortValue()
	{
		if(shortValue != null)
		{
			return shortValue;
		}
		throw new ConfigTypeNotMatchedException(strValue, "short/Short");
	}
	
	public long longValue()
	{
		if(longValue != null)
		{
			return longValue;
		}
		throw new ConfigTypeNotMatchedException(strValue, "long/Long");
	}
	

	

	private void setSystemDefault(){
		this.boolValue   = false;
		this.doubleValue = 0.0;
		this.floatValue  = 0.0f;
		this.intValue    = 0;
		this.longValue   = 0l;
		this.shortValue  = 0;
		this.strValue    = null;
		this.lastUpdateTime = System.nanoTime();
	}


	public void setDefinition(String definition){
		this.definition = definition;
	}

	public String getDefinition() {
		return definition;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getKey() {
		return key;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public Set<String> getParentConfigKeys() {
		return parentConfigKeys;
	}

	public Set<String> getChildConfigKeys() {
		return childConfigKeys;
	}
}
