package com.github.tbwork.anole.loader.util;

import com.github.tbwork.anole.loader.util.json.GsonRepository;
import com.github.tbwork.anole.loader.util.json.TypeReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Intend to replace FastJSON.
 * Please use JsonElement, instead of JSONObject in fastjson,
 * use JsonArray, rather than JSONArray in fastjson.
 */
public class JSON {




    /**
     * Parse the candidate json string to a com.google.gson.JsonObject object.
     *
     * @param jsonString the json string
     * @return
     */
    public static Object parse(String jsonString){

        return GsonRepository.gson.fromJson(jsonString, JsonObject.class);

    }

    /**
     * Parse the candidate json string to a com.google.gson.JsonObject object.
     *
     * @param jsonString the json string
     * @return
     */
    public static Object parseObject(String jsonString){

        return GsonRepository.gson.fromJson(jsonString, JsonObject.class);

    }


    /**
     * Parse target json string to a specified class object.
     *
     * @param jsonString the candidate json String
     * @param clazz the specified class object.
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz){

        return (T) GsonRepository.gson.fromJson(jsonString, clazz);

    }


    /**
     *  Parse target json element to a specified class object.
     *
     * @param jsonObject the target json object.
     * @param clazz the specified class object.
     * @param <T>
     * @return
     */
    public static <T> T parseObject(JsonObject jsonObject, Class<T> clazz){

        return (T) GsonRepository.gson.fromJson(jsonObject, clazz);

    }


    /**
     * Parse json string to java object instance with specified type reference.
     *
     * @param jsonString the candidate json string
     * @param typeReference type reference of the generic class type
     * @param <T> the generic class type
     * @return the instance of the generic class type
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> typeReference){
        return (T) GsonRepository.gson.fromJson(jsonString, typeReference.getType());
    }


    /**
     * Parse json element to java object instance with specified type reference.
     *
     * @param jsonObject the candidate json element
     * @param typeReference type reference of the generic class type
     * @param <T> the generic class type
     * @return the instance of the generic class type
     */
    public static <T> T parseObject(JsonObject jsonObject, TypeReference<T> typeReference){
        return (T) GsonRepository.gson.fromJson(jsonObject, typeReference.getType());
    }



    /**
     * Parse the target json string to a json array.
     *
     * @param jsonString the target json string
     * @return a JsonArray object
     */
    public static JsonArray parseArray(String jsonString){

        List<JsonObject> jsonObjects = parseArray(jsonString, JsonObject.class);
        JsonArray result = new JsonArray();
        if(jsonObjects == null) {
            return result;
        }
        for (JsonObject jsonObject : jsonObjects) {
            result.add(jsonObject);
        }

        return result;
    }


    /**
     * This function provides an easy way to parse json string
     * to the array list with specified type.<br/>
     * Notes: this function is less efficient than {@link #parseObject(String, TypeReference)} }.<br/>
     * You maybe prefer that in some situations which extremely requires for performance.
     * @param jsonString the candidate json string.
     * @param clazz the element class type.
     * @param <T> the element class type.
     * @return the array list.
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz){
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return GsonRepository.gson.fromJson(jsonString, type);
    }


    /**
     * Serialize the target object to a json string.
     *
     * @param target the target object
     * @return  the json string.
     */
    public static String toJSONString(Object target){

        return GsonRepository.gson.toJson(target);

    }

    /**
     * Serialize the target object to a json element.
     *
     * @param target the target object
     * @return  the json element.
     */
    public static JsonObject toJSON(Object target){

        return GsonRepository.gson.toJsonTree(target).getAsJsonObject();

    }



    /**
     * Serialize the target object to a formatted json string.
     *
     * @param target the target object
     * @return  the json string.
     */
    public static String toJSONString(Object target, boolean formatted){

        return formatted? GsonRepository.formattedGson.toJson(target) : GsonRepository.gson.toJson(target);
    }

}
