package org.tbwork.anole.loader;

import org.tbwork.anole.loader.core.loader.seeker.OmniSeeker;
import org.tbwork.anole.loader.core.loader.seeker.impl.OmniSeekerImpl;

import java.io.InputStream;
import java.util.Map;

/**
 * @program: anole-loader
 * @description: Omni seek test
 * @author: tommy.tb
 * @create: 2019-03-05 16:42
 **/
public class TestOmniSeeker {


    public static void main(String[] args) {

        OmniSeeker os = new OmniSeekerImpl("/Users/tangbo/test/*/*/*.anole",
                "/Users/tangbo/test/*/*/*/*.anole");

        long time = System.currentTimeMillis();
        Map<String, InputStream> result =  os.seekFiles();

        System.out.println("找到以下文件, 耗时" + ( System.currentTimeMillis() - time ));
        for(Map.Entry<String,InputStream> item : result.entrySet()){
            System.out.println(item.getKey());
        }
    }
}
