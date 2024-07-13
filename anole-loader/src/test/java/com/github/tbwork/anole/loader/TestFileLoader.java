package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.util.AnoleFileUtil;

public class TestFileLoader {


    public static void main(String[] args) {
        AnoleFileUtil.loadFileStreamFromJar("/Users/tommy/Documents/tools/flap-app/account-server-0.0.1-SNAPSHOT.jar!/BOOT-INF!/lib!/*.jar!/*.anole");
    }

}
