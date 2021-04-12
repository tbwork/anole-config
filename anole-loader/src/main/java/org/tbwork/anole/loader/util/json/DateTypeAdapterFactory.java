package org.tbwork.anole.loader.util.json;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * This adapter factory is used to adapt the long timestamp and formatted date
 * when parsing json string to a date.
 */
public class DateTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        if(type.getRawType().isAssignableFrom(Date.class)){
            final TypeAdapter<Date> oldInstance = gson.getDelegateAdapter(this, new TypeToken<Date>(){});
            TypeAdapter<Date> newInstance = new TypeAdapter<Date>() {
                public void write(JsonWriter out, Date value) throws IOException {
                    oldInstance.write(out, value);
                }
                public Date read(JsonReader in) throws IOException {
                    try {
                        in.nextNull();
                    } catch (Exception ex) {
                        String valueString = in.nextString();
                        if (isNumeric(valueString)) {
                            return new Date(Long.valueOf(valueString) * 1000);
                        }
                        return oldInstance.read(in);
                    }
                    return null;
                }
            };
            return (TypeAdapter) newInstance;

        }
        else{
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return delegate;
        }
    }


    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}