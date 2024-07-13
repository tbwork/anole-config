package com.github.tbwork.anole.loader.util.json;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                        return parseDate(valueString);
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

    private static Date parseDate(String source) {
        if(source == null || source.isEmpty()) return null;

        int index = 0;
        StringBuffer pattern = new StringBuffer();
        char[] chars = source.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                switch (index) {
                    case 0:
                        pattern.append("y");
                        break;
                    case 1:
                        pattern.append("M");
                        break;
                    case 2:
                        pattern.append("d");
                        break;
                    case 3:
                        pattern.append("H");
                        break;
                    case 4:
                        pattern.append("m");
                        break;
                    case 5:
                        pattern.append("s");
                        break;
                    case 6:
                        pattern.append("S");
                        break;
                }
            } else{
                pattern.append(chars[i]);
                index++;
            }
        }

        try {
            return new SimpleDateFormat(pattern.toString()).parse(source);
        } catch (ParseException e) {
            return null;
        }
    }
}