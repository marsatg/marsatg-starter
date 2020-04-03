package org.marsatg.poseidon;


import org.apache.commons.lang3.StringUtils;
import org.marsatg.NodeMethod;
import org.marsatg.annotation.Method;
import org.marsatg.annotation.Service;
import org.marsatg.proxy.BeanProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.util.*;


public class PoseidonServiceDefinition {

    public static Logger logger = LoggerFactory.getLogger(PoseidonServiceDefinition.class);


    public static Map<String, NodeMethod> getMethodMap(Object api, Class<?> cls) {
        Service serviceAnnotation = cls.getAnnotation(Service.class);
        String typeName = cls.getTypeName();
        String value = serviceAnnotation.value();
        Assert.state(StringUtils.isNotBlank(value), "@org.marsatg.annotation.Service value() nust not be null");
        Map<String, NodeMethod> map = new HashMap<>();
        try {
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            for (java.lang.reflect.Method m : methods) {
                Method annotation = m.getAnnotation(Method.class);
                if (annotation != null) {
                    String rpcMethodName = annotation.value();
                    if (StringUtils.isBlank(rpcMethodName)) {
                        rpcMethodName = m.getName();
                    }
                    NodeMethod node = map.get(rpcMethodName);
                    if (node != null) {
                        throw new Exception(" 在服务：" + cls + "中，出现了重复定义的 @Method :" + rpcMethodName);
                    }
                    Class<?>[] parameterTypes = m.getParameterTypes();
                    Annotation[][] parameterAnnotations = m.getParameterAnnotations();
                    checkMethodParameter(m, parameterTypes, parameterAnnotations);
                    String uniqeName = typeName + "." + rpcMethodName;
                    if (!value.startsWith("/")) {
                        value = "/" + value;
                    }
                    if (!value.endsWith("/")) {
                        value = value + "/";
                    }
                    String urlName = value + rpcMethodName;
                    logger.info("Netty Mapped \"{["+urlName+ "]}\" onto "+m);
                    NodeMethod nodeMethod = new NodeMethod(api, uniqeName, urlName, m, parameterAnnotations,parameterTypes);
                    BeanProxyFactory.setUrlMap(urlName, nodeMethod);
                    map.put(rpcMethodName, nodeMethod);
                }
            }
        } catch (Exception e) {
            logger.info("\n\n");
            logger.info(" ==================== Exception: PoseidonServiceDefinition.getMethodMap() in class: " + cls.getName());
            logger.info(e.getMessage());
            logger.info("\n\n");
            System.exit(-1);
        }
        return map;
    }


    public static void checkMethodParameter(java.lang.reflect.Method method, Class<?>[] params, Annotation[][] annotations) throws Exception {
        if (params == null || annotations == null || params.length == 0 || annotations.length == 0) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            Class<?> paramClass = params[i];
            Annotation[] array = annotations[i];
            if (!isBaseParameterType(paramClass)) {
                if (array == null || array.length == 0) {
                    throw new Exception("方法 -> " + method.getName() + "  非基本参数类型 " + paramClass.getName() + " 缺少 @RequestBody 注解");
                }
                if (array.length > 1) {
                    throw new Exception("方法 -> " + method.getName() + "  非基本参数类型 " + paramClass.getName() + " 只能由一个 @RequestBody 注解修饰");
                }
                if (array[0].annotationType() != RequestBody.class) {
                    throw new Exception("方法 -> " + method.getName() + "  非基本参数类型 " + paramClass.getName() + " 缺少 @RequestBody 注解");
                }
            }else{
                if(array.length > 0){
                    throw new Exception("方法 -> " + method.getName() + "  基本参数类型 " + paramClass.getName() + " 不允许任何参数注解:"+Arrays.asList(array).toString());
                }
            }
        }
    }

    public static boolean isBaseParameterType(Class<?> cls) {
        return baseParameterClass.contains(cls);
    }


    public static Set<Class> baseParameterClass = new HashSet<>(Arrays.asList(int.class, long.class,
            short.class, double.class, float.class, boolean.class, byte.class, char.class,
            Short.class, Integer.class, Long.class, Double.class, String.class));






}
