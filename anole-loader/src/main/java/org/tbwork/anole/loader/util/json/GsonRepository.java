package org.tbwork.anole.loader.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonRepository {



    public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new DateTypeAdapterFactory()).setDateFormat("yyyy-MM-dd HH:mm:ss").setExclusionStrategies(new CustomExclusionStrategies()).serializeNulls().create();

    public static final Gson formattedGson = new GsonBuilder().registerTypeAdapterFactory(new DateTypeAdapterFactory()).setDateFormat("yyyy-MM-dd HH:mm:ss").setExclusionStrategies(new CustomExclusionStrategies()).serializeNulls().setPrettyPrinting().create();


}
