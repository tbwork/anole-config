package com.github.tbwork.anole.loader.util;

import com.github.tbwork.anole.loader.enums.OsCategory;

import java.util.List;

/**
 * Path util.
 */
public class PathUtil {


    public static String toLinuxStylePath(String path){
        path = format2Slash(path);
        if(!path.startsWith("/"))
            path = "/"+path;
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


    public static boolean directoryMatch(String targetPath, String directoryPattern){
        AnoleFilePath anoleFilePath = new AnoleFilePath(targetPath);
        return anoleFilePath.directoryMatch(directoryPattern);
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
    public static boolean asteriskMatch(String asteriskPath, String targetPath){
        AnoleFilePath afp1 = new AnoleFilePath(asteriskPath);
        AnoleFilePath afp2 = new AnoleFilePath(targetPath);
        return afp1.match(afp2) ;
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
    public static boolean asteriskPreMatch(String asteriskPath, String targetPath){
        AnoleFilePath afp1 = new AnoleFilePath(asteriskPath);
        AnoleFilePath afp2 = new AnoleFilePath(targetPath);
        return afp1.preMatch(afp2);
    }


    public static String getSolidDirectory(String fullPath){
        AnoleFilePath afp = new AnoleFilePath(fullPath);
        return afp.getSolidDirectory();
    }

    public static boolean isFuzzyDirectory(String fullPath){
        AnoleFilePath afp = new AnoleFilePath(fullPath);
        return afp.isFuzzyDirectory();
    }


    public static class AnoleFilePath {

        private List<String> pathPartList;

        public AnoleFilePath(String fullPath){
            if(fullPath.startsWith("\\") || fullPath.startsWith("/")){
                fullPath = fullPath.substring(1);
            }
            pathPartList = SetUtil.newArrayList(fullPath.split("\\\\|/"));
        }

        public boolean isFuzzyDirectory(){
            int i = 0;
            for(i = 0; i < pathPartList.size(); i++){
                if(pathPartList.get(i).contains("*")){
                    break;
                }
            }
            return i < pathPartList.size()-1;
        }

        public boolean directoryMatch(String directoryPattern){
            for(String part : pathPartList){
                if(S.asteriskMatch(directoryPattern, part)){
                    return true;
                }
            }
            return false;
        }

        public String getSolidDirectory(){
            int i = 0;
            for(i = 0; i < pathPartList.size(); i++){
                if(pathPartList.get(i).contains("*")){
                    break;
                }
            }
            return "/" + S.join("/", pathPartList.subList(0, Math.min(i, pathPartList.size()-1))) + "/";
        }

        public boolean match(AnoleFilePath target){


            if( this.pathPartList.size() != target.pathPartList.size()){
                return false;
            }
            for(int i = 0 ; i < this.pathPartList.size(); i ++){
                if(!S.asteriskMatch(this.pathPartList.get(i), target.pathPartList.get(i))){
                    return false;
                }
            }
            return true;
        }

        public boolean preMatch(AnoleFilePath target){
            for(int i = 0 ; i < Math.min(this.pathPartList.size(), target.pathPartList.size()); i ++){
                if(!S.asteriskMatch(this.pathPartList.get(i), target.pathPartList.get(i))){
                    return false;
                }
            }
            return true;
        }
    }

    public static String uniformAbsolutePath(String absolutePath) {
        String result =  getNakedAbsolutePath(format2Slash(absolutePath));
        if(!result.startsWith("/"))
            return "/"+result;
        return result;
    }


}
