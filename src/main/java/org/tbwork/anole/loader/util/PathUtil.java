package org.tbwork.anole.loader.util;

import lombok.Data;
import org.tbwork.anole.loader.enums.OsCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @program: anole-loader
 * @description: Utilities for path operation
 * @author: tommy.tb
 * @create: 2019-03-06 13:46
 **/
public class PathUtil {



    public static String getSolidDirectory(String fullPath){
        AnoleFilePath afp = new AnoleFilePath(fullPath);
        return afp.getSolidDirectory();
    }

    public static boolean isFuzzyDirectory(String fullPath){
        AnoleFilePath afp = new AnoleFilePath(fullPath);
        return afp.isPattern();
    }

    /**
     * Uniform the candidate path according to the Linux style.
     * @param path the candidate path like "C:/aa\bbb/cc.txt"
     * @return like "/C:/aa/bbb/cc.txt"
     */
    public static String toLinuxStylePath(String path){
        path = format2Slash(path);
        if(!path.startsWith("/"))
            path = StringUtil.concat("/", path);
        return path;
    }


    public static String format2Slash(String path){
        return path.replace("\\\\", "/").replace("\\", "/");
    }

    public static String [] format2SlashPathes(String ... pathes) {
        if(pathes == null)
            return null;
        String [] result = new String[pathes.length];
        for(int i = 0; i < pathes.length; i++) {
            result[i] = format2Slash(pathes[i]);
        }
        return result;
    }

    public static String getNakedAbsolutePath(String absolutePath) {
        return absolutePath.replace("file:", "").replace("jar:","");
    }


    public static boolean isAbsolutePath(String path) {
        if(OsUtil.getOsCategory().equals(OsCategory.WINDOWS)) {
            return path.contains(":/") || path.contains(":\\");
        }
        else {
            return path.startsWith("/") || path.startsWith("\\");
        }
    }

    /**
     * Match the asterisk file path and the target actual file path.
     * <p>The matched result should fulfill two conditions:<br>
     * <b>1.</b>The directory depth of the asterisk file is same to the asterisk file path. <br>
     * <b>2.</b>The target file path matches the asterisk file in text. <br>
     * @param asteriskPath the asterisk file's full path
     * @param targetPath the target actual file's full path
     * @return true means matched successfully, otherwise means failed.
     */
    public static boolean asteriskMatchPath(String asteriskPath, String targetPath){
        AnoleFilePath afp1 = new AnoleFilePath(asteriskPath);
        AnoleFilePath afp2 = new AnoleFilePath(targetPath);
        return afp1.matchSize(afp2) && StringUtil.asteriskMatch(asteriskPath, targetPath);
    }


    /**
     * <pre>
     *     E.g.
     *     Input: preMatchPattern("&#47;a&#47;b&#47;c&#47;","&#47;a&#47;*&#47;c&#47;");
     *     Output: true
     *
     * </pre>
     * @param candidate the candidate string
     * @param pattern the full-path pattern.
     * @return true if the candidate fulfill the head of pattern, otherwise return false
     */
    public static boolean preMatchPattern(String candidate, String pattern){
        AnoleFilePath afpCandidate = new AnoleFilePath(candidate);
        AnoleFilePath afpPattern = new AnoleFilePath(pattern);

        List<String> candidateParts = afpCandidate.getPathPartList();
        List<String> patternParts = afpPattern.getPathPartList();
        if(candidateParts.size() > patternParts.size())
            return false;
        for(int i = 0 ; i < candidateParts.size(); i ++){
            String candidatePart = candidateParts.get(i);
            String patternPart = patternParts.get(i);
            if(!StringUtil.asteriskMatch(patternPart, candidatePart)){
                return false;
            }
        }
        return true;
    }


    public static String removeRepeatSlash(String path){
        return path.replaceAll("/+", "/");
    }

    public static String buildPath(String rootDirectory, String subPath){
        String result =  StringUtil.concat(rootDirectory, "/", subPath);
        return uniformPath(result);
    }

    public static String uniformPath(String path){
        AnoleFilePath anoleFilePath = new AnoleFilePath(path);
        return anoleFilePath.getUniformPath();
    }

    @Data
    private static class AnoleFilePath {

        private List<String> pathPartList;

        private boolean isTailSlash = false;

        public AnoleFilePath(String fullPath){
            fullPath = fullPath.replace("jar!/", "jar/");
            isTailSlash = fullPath.endsWith("/");
            if(fullPath.startsWith("/")){
                fullPath = fullPath.substring(1);
            }
            if(fullPath.endsWith("/")){
                fullPath = fullPath.substring(0, fullPath.length() - 1);
            }
            fullPath = PathUtil.removeRepeatSlash(fullPath);
            pathPartList = SetUtil.newArrayList(fullPath.split("\\\\|/"));
            removeRelativePath();
        }

        public boolean isPattern(){
            int i = 0;
            for(i = 0; i < pathPartList.size(); i++){
                if(pathPartList.get(i).contains("*")){
                    break;
                }
            }
            return i < pathPartList.size()-1;
        }

        public String getSolidDirectory(){
            int i = 0;
            for(i = 0; i < pathPartList.size(); i++){
                if(pathPartList.get(i).contains("*")){
                    break;
                }
            }
            String solidParts = StringUtil.join("/", pathPartList.subList(0, Math.min(i, pathPartList.size()-1)));
            solidParts = toLinuxStylePath(solidParts);
            if(isTailSlash){
                return StringUtil.concat(solidParts, "/");
            }
            else{
                return solidParts;
            }
        }

        public String getUniformPath(){
            return toLinuxStylePath(StringUtil.join("/", pathPartList ));
        }


        /**
         * Remove relative paths like "/a/b/../../c"
         * @return "/c"
         */
        private void removeRelativePath(){
            Stack<String> stack = new Stack<>();
            for(String part : pathPartList){
                if("..".equals(part)){
                    stack.pop();
                }
                else{
                    stack.push(part);
                }
            }
            List<String> result = new ArrayList<>();
            for(String part : stack){
                result.add(part);
            }
            pathPartList = result;
        }

        public boolean matchSize(AnoleFilePath afp){
            return this.pathPartList.size() == afp.pathPartList.size();
        }
    }
}
