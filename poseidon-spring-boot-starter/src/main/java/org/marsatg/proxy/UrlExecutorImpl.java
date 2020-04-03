package org.marsatg.proxy;

import com.alibaba.fastjson.JSON;
import org.marsatg.ExceptionUtils;
import org.marsatg.NodeMethod;
import org.marsatg.executors.UrlExecutor;
import org.marsatg.http.ResponseHolder;
import org.marsatg.netty.NettyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;


@Service
public class UrlExecutorImpl implements UrlExecutor {

    private static Logger logger = LoggerFactory.getLogger(UrlExecutorImpl.class);
    @Autowired
    NettyProperties properties;

    @Override
    public ResponseHolder invoke(String url, Object... args) {
        logger.info("NettyServer-recieved 【" + url + "】 parameter-length : " + args.length + "");
        // TODO 1 NodeMethod 判断
        NodeMethod nodeMethod = BeanProxyFactory.getNodeMethod(url);
        if (nodeMethod == null) {
            StringBuilder message = new StringBuilder(" {\"message\":\"");
            message.append("not find url:").append(url).append("\"}");
            logger.info(message.toString());
            return ResponseHolder.getErrorResponse(message.toString());
        }
        // TODO 2 判断参数长度
        Class<?>[] parameterTypes = nodeMethod.getParameterTypes();
        if (parameterTypes.length != args.length) {
            StringBuilder message = new StringBuilder(" {\"message\":\"");
            message.append("parameters not match, expect:").append(parameterTypes.length).append(", but:").append(args.length).append(", method:" + nodeMethod.getMethod().getName()).append("\"}");
            logger.info(message.toString());
            return ResponseHolder.getErrorResponse(message.toString());
        }
        // TODO 3 参数注解类型转换
        Annotation[][] parameterAnnotations = nodeMethod.getAnnotationArray();
        if (parameterAnnotations != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Class sourceType = parameterTypes[i];
                Annotation[] array = parameterAnnotations[i];
                if (array != null && array.length > 0 && array[0].annotationType() == RequestBody.class) {
                    args[i] = JSON.parseObject(args[i].toString(), sourceType);
                }
            }
            if (properties.isDebugParameter()) {
                logger.info("parameters: " + Arrays.asList(args));
            }
        }
        // TODO 4 invoke
        Object result = null;
        try {
            result = nodeMethod.invoke(args);
        } catch (Exception e) {
            String exceptionMessage = ExceptionUtils.getCauses(e);
            logger.info(exceptionMessage);
            logger.error("Exception:", e);
            return ResponseHolder.getErrorResponse(exceptionMessage);
        }
        // TODO 5 invoke - successs
        return ResponseHolder.getSuccessResponse(result);
    }
}
