package org.marsatg;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class ExceptionUtils {

    private static Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    public static void printStackTrace(Exception e) {
        logger.error("error", e);
    }

    public static String getCauses(Exception e) {
        ArrayList<String> cause = new ArrayList<>();
        getInvocationCauses(e, cause);
        Map<String,Object> map = new HashMap<>();
        if(cause.size()>0){
            map.put("message",cause.get(0));
        }else {
            map.put("message","执行异常");
        }
        if(cause.size()>1){
            map.put("stackMessage",cause);
        }
        return JSON.toJSONString(map);
    }

    private static void getInvocationCauses(Throwable e, List<String> list) {
        if (!(e instanceof InvocationTargetException)) {
            list.add(e.getMessage());
        }
        Throwable t = e.getCause();
        if (t != null) {
            getInvocationCauses(t, list);
        }

    }


}
