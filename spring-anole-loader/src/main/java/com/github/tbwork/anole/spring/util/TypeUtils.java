package com.github.tbwork.anole.spring.util;

import com.github.tbwork.anole.loader.util.JSON;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Type converter utils.
 */
public class TypeUtils {


    /**
     * Convert one value to another type.
     * @param val the candidate value
     * @param requiredType the specified type
     * @return the value as an instance of given type
     */
    public static Object convert(Object val, Class requiredType){

        if(isString(requiredType)){
            if(val.getClass().isAssignableFrom(String.class)){
                return val;
            }
            else{
                return val.toString();
            }
        }
        else if(isInt(requiredType)){
            if(val.getClass().isAssignableFrom(Integer.class)){
                return val;
            }
            else{
                return Integer.valueOf(val.toString());
            }
        }
        else if(isBoolean(requiredType)){
            if(val.getClass().isAssignableFrom(Boolean.class)){
                return val;
            }
            else{
                return Boolean.valueOf(val.toString());
            }
        }
        else if(isByte(requiredType)){
            if(val.getClass().isAssignableFrom(Byte.class)){
                return val;
            }
            else{
                return Byte.valueOf(val.toString());
            }
        }
        else if( isChar(requiredType)){
            if (val.getClass().isAssignableFrom(Character.class)){
                return val;
            }
            else if(val.toString().length() > 0){
                return val.toString().charAt(0);
            }
        }
        else if( isDecimal(requiredType)){
            if(val.getClass().isAssignableFrom(BigDecimal.class)){
                return val;
            }
            else{
                return new BigDecimal(val.toString());
            }
        }
        else if(isDouble(requiredType)){
            if(val.getClass().isAssignableFrom(Double.class)){
                return val;
            }
            else{
                return Double.valueOf(val.toString());
            }
        }
        else if(isFloat(requiredType)){
            if(val.getClass().isAssignableFrom(Float.class)){
                return val;
            }
            else{
                return Float.valueOf(val.toString());
            }
        }
        else if(isLong(requiredType)){
            if(val.getClass().isAssignableFrom(Long.class)){
                return val;
            }
            else{
                return Long.valueOf(val.toString());
            }
        }
        else if(isShort(requiredType)){
            if(val.getClass().isAssignableFrom(Short.class)){
                return val;
            }
            else{
                return Short.valueOf(val.toString());
            }
        }
        else {
            String jsonValue = val.toString().trim();
            if(jsonValue.startsWith("[")){
                return JSON.parseArray(jsonValue, requiredType);
            }
            else{
                return JSON.parseObject(jsonValue, requiredType);
            }

        }
        return null;
    }

    public static boolean isInt(Class requiredType){
        return requiredType.getTypeName() == "int"
                || requiredType.isAssignableFrom(Integer.class)
                || requiredType.isAssignableFrom(BigInteger.class);
    }

    public static boolean isFloat(Class requiredType){
        return requiredType.getTypeName() == "float" || requiredType.isAssignableFrom(Float.class);
    }

    public static boolean isShort(Class requiredType){
        return requiredType.getTypeName() == "short" || requiredType.isAssignableFrom(Short.class);
    }

    public static boolean isLong(Class requiredType){
        return requiredType.getTypeName() == "long" || requiredType.isAssignableFrom(Long.class);
    }


    public static boolean isByte(Class requiredType){
        return requiredType.getTypeName() == "byte" || requiredType.isAssignableFrom(Byte.class);
    }

    public static boolean isString(Class requiredType){
        return  requiredType.isAssignableFrom(String.class);
    }

    public static boolean isChar(Class requiredType){
        return requiredType.getTypeName() == "char" || requiredType.isAssignableFrom(Character.class);
    }

    public static boolean isBoolean(Class requiredType){
        return requiredType.getTypeName() == "boolean" || requiredType.isAssignableFrom(Boolean.class);
    }

    public static boolean isDouble(Class requiredType){
        return requiredType.getTypeName() == "double" || requiredType.isAssignableFrom(Double.class);
    }


    public static boolean isDecimal(Class requiredType){
        return  requiredType.isAssignableFrom(BigDecimal.class);
    }



}
