package org.marsatg.proxy;

import com.alibaba.fastjson.JSON;
import org.marsatg.ExceptionUtils;
import org.marsatg.NodeMethod;

import org.marsatg.executors.NameExecutor;
import org.marsatg.http.ResponseHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;


@Service
public class NameExecutorImpl implements NameExecutor {

    private static Logger logger = LoggerFactory.getLogger(BeanProxyFactory.class);


    @Override
    public ResponseHolder invoke(String consumerName, String serviceName, String methodName, Object... args) {
        // TODO 1 服务判空
        StringBuilder message = new StringBuilder(" {\"message\":\"");
        Map<String, NodeMethod> serviceMethodMap = BeanProxyFactory.getMethodMap().get(serviceName);
        if (serviceMethodMap == null || serviceMethodMap.size() == 0) {
            message.append("not find service:").append(serviceName).append("\"}");
            logger.info(message.toString());
            return ResponseHolder.getErrorResponse(message.toString());
        }
        // TODO 2 方法判空
        NodeMethod nodeMethod = serviceMethodMap.get(methodName);
        if (nodeMethod == null) {
            message.append("not find method:").append(methodName).append("\"}");
            logger.info(message.toString());
            return ResponseHolder.getErrorResponse(message.toString());
        }
        Method method = nodeMethod.getMethod();
        // TODO 3 判断参数长度
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != args.length) {
            message.append("parameters not match, expect:").append(parameterTypes.length).append(", but:").append(args.length).append(", method:"+methodName).append("\"}");
            logger.info(message.toString());
            return ResponseHolder.getErrorResponse(message.toString());
        }
        // TODO 4 参数注解类型转换
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Class sourceType = parameterTypes[i];
                Annotation[] array = parameterAnnotations[i];
                if (array != null && array.length > 0 && array[0].annotationType() == RequestBody.class) {
                    args[i] = JSON.parseObject(args[i].toString(), sourceType);
                }
            }
        }
        // TODO 5 invoke
        Object callApi = BeanProxyFactory.getServerApiBeanMap().get(serviceName);
        Object result = null;
        try {
            result = method.invoke(callApi, args);
        } catch (Exception e) {
            String exceptionMessage = ExceptionUtils.getCauses(e);
            logger.info(exceptionMessage);
            logger.error("Exception:", e);
            return ResponseHolder.getErrorResponse(exceptionMessage);
        }
        // TODO 6 invoke - successs
        String loggerArgs = "[]";
        if (args != null) {
            loggerArgs = Arrays.asList(args).toString();
        }
        logger.info("NettyServer-invoke(success) consumer:" + consumerName + " 参数 -> " + loggerArgs);
        return ResponseHolder.getSuccessResponse(result);
    }

}
