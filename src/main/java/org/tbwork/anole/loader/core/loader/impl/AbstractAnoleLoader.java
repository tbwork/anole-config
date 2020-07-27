package org.tbwork.anole.loader.core.loader.impl;

import org.tbwork.anole.loader.context.Anole;
import org.tbwork.anole.loader.core.loader.AnoleLoader;
import org.tbwork.anole.loader.core.model.RawKV;
import org.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import org.tbwork.anole.loader.core.model.ConfigFileResource;
import org.tbwork.anole.loader.core.register.AnoleConfigRegister;
import org.tbwork.anole.loader.core.resource.ResourceLoader;
import org.tbwork.anole.loader.enums.FileLoadStatus;
import org.tbwork.anole.loader.util.AnoleLogger;
import org.tbwork.anole.loader.util.StringUtil;

import java.io.InputStream;
import java.util.*;

public abstract class AbstractAnoleLoader implements AnoleLoader {


    private AnoleConfigFileParser anoleConfigFileParser;

    private ResourceLoader resourceLoader;

    private AnoleConfigRegister anoleConfigRegister;


    protected AbstractAnoleLoader(AnoleConfigFileParser parser, ResourceLoader resourceLoader, AnoleConfigRegister anoleConfigRegister){
        this.anoleConfigFileParser = parser;
        this.resourceLoader = resourceLoader;
        this.anoleConfigRegister = anoleConfigRegister;
    }


    @Override
    public void load(String... configLocations) {

        // logo print
        LogoUtil.decompress("/logo.cps",  "::Anole Loader::   (v1.2.8)");

        // locate and load as input stream
        ConfigFileResource [] configFileResources = resourceLoader.load(configLocations);

        // parse to kvs
        List<RawKV> rawKVS =  parse(configFileResources);

        // register
        anoleConfigRegister.register(rawKVS);

        Anole.initialized = true;

        AnoleLogger.info("[:)] Anole configurations are loaded successfully.");
    }



    private List<RawKV> parse(ConfigFileResource [] configFileResources){

        Arrays.sort(configFileResources, new Comparator<ConfigFileResource>() {
            @Override
            public int compare(ConfigFileResource o1, ConfigFileResource o2) {
                return o1.getOrder().compareTo(o2.getOrder());
            }
        });

        List<RawKV> result = new ArrayList<>();

        for(ConfigFileResource configFileResource : configFileResources){

            result.addAll(anoleConfigFileParser.parse(configFileResource.getInputStream(),
                    configFileResource.getFullPath()));
        }
        return result;

    }



    private static class  LogoUtil{

        private static void print(String str) {
            System.out.print(str);
        }
        private static void print(char khar) {
            System.out.print(khar);
        }
        private static void println(String str) {
            System.out.println(str);
        }
        private static void println(char khar) {
            System.out.println(khar);
        }

        public static void decompress(String filePath, String message){
            InputStream in = AnoleFileLoader.class.getResourceAsStream(filePath);
            Scanner scanner = null;
            List<Integer> chars = new ArrayList<Integer>();
            try {
                scanner = new Scanner(in);
                while(scanner.hasNextLine()){
                    String lineStr = scanner.nextLine();
                    String [] charAndRepeatCount = lineStr.split(",");
                    Integer targetChar =  Integer.valueOf(charAndRepeatCount[0]);
                    int repeatCount = Integer.valueOf(charAndRepeatCount[1]);
                    for(int i = 0; i < repeatCount ; i++){
                        chars.add(targetChar);
                    }
                }
                scanner.close();
                setFrameChar(chars, '*');
                addCustomContet(chars, '?', message, ' ');
                print(chars);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void print(List<Integer> chars){
            for(int i = 0 ; i< chars.size(); i++){
                String temp = String.valueOf((char)chars.get(i).byteValue());
                if(temp.contains("\n")) {
                    System.out.println("");
                }else {
                    System.out.print(temp);
                }
            }
        }


        private static void addCustomContet(List<Integer> chars, char placeHolderChar, String customString, char blankChar){
            StringBuilder sb = new StringBuilder();
            int start = -1;
            for(int i =0; i < chars.size(); i++){
                if(chars.get(i)==placeHolderChar){
                    if(start == -1){
                        start = i;
                    }
                    sb.append(placeHolderChar);
                }
            }
            String customPlaceholder = sb.toString();
            int customSize = customPlaceholder.length();
            if(customString.length() > customSize){
                customString = customString.substring(0, customSize);
            }
            int blankSize = customSize - customString.length();
            int foreBlankSize = blankSize/2;
            int tailBlankSize = blankSize - foreBlankSize;
            customString = StringUtil.getRepeatCharString(blankChar, foreBlankSize) + customString + StringUtil.getRepeatCharString(blankChar, tailBlankSize);
            for(int i = start; i < start + customSize;  i ++){
                chars.set(i, (int) customString.charAt(i-start));
            }
        }

        private static void setFrameChar(List<Integer> chars, char targetChar){
            if(targetChar == '-' || targetChar == '#' || targetChar == '?'){
                System.out.println("Invalid frame char. It can not be '-', '#' or '?'");
            }
            for(int i=0; i<chars.size() ; i++){
                if(chars.get(i)=='*'){
                    chars.set(i, (int)targetChar);
                }
            }
        }

    }
}
