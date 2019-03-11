package org.tbwork.anole.loader;

import org.tbwork.anole.loader.util.StringUtil;

/**
 * @program: anole-loader
 * @description: test string util
 * @author: tommy.tb
 * @create: 2019-03-05 16:46
 **/
public class TestStringUitl {


    public static void main(String[] args) {
        String pattern = "/Users/tangbo/test/*/*/*.anole";
        String target = "/Users/tangbo/test/test.jar/resources/mz-hsf-common-logic-1.0.0-SNAPSHOT.jar/hsf-common.anole";
        System.out.println(StringUtil.asteriskMatch(pattern, target));

    }
}
