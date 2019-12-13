package org.marsatg.annotation;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public interface PoseidonServiceDefinition {

    Logger logger = LoggerFactory.getLogger(PoseidonServiceDefinition.class);



    static Map<String, java.lang.reflect.Method> getMethodMap(Class<?> cls){
        Map<String, java.lang.reflect.Method> map = new HashMap<>();
        try {
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            for(java.lang.reflect.Method m:methods){
                Method annotation = m.getAnnotation(Method.class);
                if(annotation != null){
                    String rpcMethodName = annotation.value();
                    if(StringUtils.isBlank(rpcMethodName)){
                        rpcMethodName = m.getName();
                    }
                    java.lang.reflect.Method method = map.get(rpcMethodName);
                    if(method != null){
                        throw new Exception(" 在服务："+cls+"中，出现了重复定义的 @Method :"+rpcMethodName);
                    }
                    map.put(rpcMethodName,m);
                }
            }
        }catch (Exception e){
            logger.info("\n\n");
            logger.info(" ==================== Exception: PoseidonServiceDefinition.getMethodMap() in class: "+cls.getName());
            logger.info(e.getMessage());
            logger.info("\n\n");
            System.exit(-1);
        }
        return map;
    }
}
