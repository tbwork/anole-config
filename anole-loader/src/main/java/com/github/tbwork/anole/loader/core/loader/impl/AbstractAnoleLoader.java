package com.github.tbwork.anole.loader.core.loader.impl;

import com.github.tbwork.anole.loader.Anole;
import com.github.tbwork.anole.loader.core.model.ConfigFileResource;
import com.github.tbwork.anole.loader.core.model.RawKV;
import com.github.tbwork.anole.loader.core.parser.AnoleConfigFileParser;
import com.github.tbwork.anole.loader.core.register.AnoleConfigRegister;
import com.github.tbwork.anole.loader.core.resource.ResourceLoader;
import com.github.tbwork.anole.loader.statics.StaticValueBook;
import com.github.tbwork.anole.loader.util.AnoleLogger;
import com.github.tbwork.anole.loader.util.S;
import com.github.tbwork.anole.loader.core.loader.AnoleLoader;

import java.io.InputStream;
import java.util.*;

public abstract class AbstractAnoleLoader implements AnoleLoader {

    private final AnoleLogger logger = new AnoleLogger(getClass());

    private AnoleConfigFileParser anoleConfigFileParser;

    private ResourceLoader resourceLoader;

    private AnoleConfigRegister anoleConfigRegister;


    protected AbstractAnoleLoader(String environment, ResourceLoader resourceLoader, AnoleConfigRegister anoleConfigRegister){
        this.anoleConfigFileParser =  AnoleConfigFileParser.getInstance(environment);
        this.resourceLoader = resourceLoader;
        this.anoleConfigRegister = anoleConfigRegister;
    }


    @Override
    public void load(String... configLocations) {

        // logo print
        LogoUtil.decompress("/logo.cps",  String.format("::Anole Loader::   (%s)", StaticValueBook.ANOLE_VERSION));

        // locate and load as input stream
        ConfigFileResource[] configFileResources = resourceLoader.load(configLocations);

        // parse to kvs
        List<RawKV> rawKVS =  parse(configFileResources);

        // register configs
        anoleConfigRegister.register(rawKVS);


        logger.info("[:)] Anole configurations are loaded successfully.");

        Anole.initialized = true;
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
            customString = S.getRepeatCharString(blankChar, foreBlankSize) + customString + S.getRepeatCharString(blankChar, tailBlankSize);
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
