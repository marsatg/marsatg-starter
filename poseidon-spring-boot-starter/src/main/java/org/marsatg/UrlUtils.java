package org.marsatg;

import org.springframework.util.Assert;

public class UrlUtils {


    public static boolean isContainSlash(String url){
        return url.contains("/");
    }

    public static void isLegalUrl(String url){
        Assert.state(!url.contains("/"),"Invaild symbol /");
        Assert.state(!url.contains("\\"),"Invaild symbol \\");
        Assert.state(!url.contains("?"),"Invaild symbol ?");
        Assert.state(!url.contains("}"),"Invaild symbol }");
        Assert.state(!url.contains("{"),"Invaild symbol {");
        Assert.state(!url.contains("."),"Invaild symbol .");
        Assert.state(!url.contains("`"),"Invaild symbol `");
    }

    public static String getStartSlashPrefix(String url){
        if(url.startsWith("/")){
            int index = url.indexOf("/",1);
            return url.substring(1,index);
        }else {
            int index = url.indexOf("/");
            return url.substring(0,index);
        }
    }


}
