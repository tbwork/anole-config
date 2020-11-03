package org.tbwork.anole.loader.core.manager.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.tbwork.anole.loader.core.manager.ConfigManager;
import org.tbwork.anole.loader.core.model.ConfigItem;
import org.tbwork.anole.loader.exceptions.ErrorSyntaxException;
import org.tbwork.anole.loader.util.S;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tools for processing anole config's value.
 */
public class AnoleValueManager {

    private final static ConfigManager configManager = AnoleConfigManager.getInstance();

    @Data
    @AllArgsConstructor
    public static class ValueDefinition {

        private List<Object> parts;

        public List<String> getReferencingKeys(){
            return parts.stream().
                    filter(part-> part instanceof Variable).
                    map(part->((Variable) part).getReferencingKey()).
                    collect(Collectors.toList());
        }

        @Override
        public String toString(){
            if(parts == null){
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for(Object part : parts){
               stringBuilder.append(part.toString());
            }
            return stringBuilder.toString();
        }
    }

    @Data
    @AllArgsConstructor
    public static class Variable{

        private String referencingKey;

        private ValueDefinition defaultValue;

        @Override
        public String toString(){
            ConfigItem referencingConfig = configManager.registerFromAnywhere(referencingKey);
            if(referencingConfig != null){
                return referencingConfig.strValue();
            }
            else{
                if( defaultValue == null ){
                    String errorMessage = String.format("There is no manual-set or default-set value for '%s'.", referencingKey);
                    throw new ErrorSyntaxException(referencingKey, errorMessage);
                }
                return defaultValue.toString();
            }
        }
    }


    /**
     * Search and return all variables in the input string.
     * E.g.
     * <pre>
     * 1. input : 123${21212}2${qwe1:1}13
     * 2. result: [{"key":"21212", "defaultValue":null}, {"key":"qwe1", "defaultValue":1}]
     * </pre>
     * @param value the input value
     * @param key for sake of trouble-shooting.
     * @return the variables referenced by the given key
     */
    public static ValueDefinition compile(String value, String key){

        List<String> parts = splitToParts(value, key);
        List<Object> compiledParts = new ArrayList<>();

        for(String stringPart : parts){
            compiledParts.add(getVariable(stringPart, key));
        }

        return new ValueDefinition(compiledParts);
    }



    /**
     * Match and return all first level variables with cloth in the input string.
     * E.g.
     * <pre>
     * 1. input : 123${21212:123${abc}}2${qwe1}13
     * 2. result: ["${21212:123${abc}}", "${qwe1}"]
     * </pre>
     * @param definition the input config definition
     * @param key for sake of trouble-shooting.
     * @return the variables with cloth
     */
    public static List<String> splitToParts(String definition, String key){
        List<String> result = new ArrayList<String>();
        int p = 0;
        int currentStackDepth = 0;
        int segmentStart = 0;
        if(definition == null)
            throw new RuntimeException("There is no manual-set or default-set value for '" + key + "'.");
        while( p < definition.length())
        {
            char icl = p > 0 ? definition.charAt(p-1) : ' ';
            char ic = definition.charAt(p);
            char icn = p < definition.length()-1 ?  definition.charAt(p+1) : ' ';
            if(checkHead(icl,ic, icn)){
                if(segmentStart < p && currentStackDepth == 0){
                    result.add(definition.substring(segmentStart, p));
                    segmentStart = p;
                }
                currentStackDepth ++;
            }
            else if( ic == '}' && icl != '\\' )  {
                if( currentStackDepth == 1 ){
                    result.add(definition.substring( segmentStart ,p+1));
                    segmentStart = p+1;
                }
                currentStackDepth --;
            }
            p ++;
        }
        if(segmentStart < definition.length()){
            result.add(definition.substring(segmentStart));
        }
        if(currentStackDepth > 0){
            String message =  "Lack of '}': an right brace '}' is needed to match the left brace'{'.";
            throw new ErrorSyntaxException(key, message);
        }
        return result;
    }

    /**
     * Get variable name from the target string.
     *
     * @param str the target string.
     * @param ownerKey the owner key, for trouble-shooting.
     * @return
     */
    public static Object getVariable(String str, String ownerKey){
        if(!str.startsWith("${")){
            return str;
        }
        String plainDefinition =  str.substring(2, str.length()-1).trim();
        String resultKey = plainDefinition;
        int index = plainDefinition.indexOf(":");
        ValueDefinition valueDefinition = null;
        if(index > -1){
            resultKey = plainDefinition.substring(0, index);
            String defaultValue = plainDefinition.substring(index+1);
            valueDefinition = compile(defaultValue, ownerKey);
        }

        if(S.isNotEmpty(ownerKey) && S.isEmpty(resultKey)){
            throw new ErrorSyntaxException(ownerKey, str + " must contain a valid variable.");
        }

        return new Variable(resultKey, valueDefinition);
    }


    /**
     * Check whether the input value contains another variable or not.
     * E.g. following example input will cause the result of true.
     * <pre>
     * 1. 123${21212}213
     * </pre>
     * @param value the input value
     * @return true if the value contains another variable or more,
     *  otherwise return false.
     */
    public static boolean containVariable(String value){
        if(S.isEmpty(value)){
            return false;
        }
        int p = 0;
        int vs = -1;
        while( p < value.length())
        {
            char icl = p > 0 ? value.charAt(p-1) : ' ';
            char ic = value.charAt(p);
            char icn = p < value.length()-1 ?  value.charAt(p+1) : ' ';
            if(checkHead(icl,ic, icn)){
                    vs = p;
            }
            else if( ic == '}' && icl != '\\')  {
                if(vs > -1){
                   return true;
                }
            }
            p ++;
        }
       return false;
    }


    public static boolean isExpression(String definition){
        if(definition == null){
            return false;
        }
        return definition.trim().startsWith("@@");
    }

    public static String getExpression(String definition){
        if(definition == null || !isExpression(definition)){
            // generally impossible
           throw new RuntimeException("Expression definition should not be empty.");
        }
        definition = definition.trim();
        return definition.substring(2);
    }


    private static boolean checkHead(char a, char b, char c){
        return a!='\\' && isHeadChar(b) && c == '{';
    }


    private static boolean isHeadChar(char a){
        return  a == '$';
    }





}
