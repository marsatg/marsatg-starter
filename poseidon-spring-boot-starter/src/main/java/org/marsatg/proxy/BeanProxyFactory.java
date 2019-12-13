package org.marsatg.proxy;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.annotation.PoseidonServiceDefinition;
import org.marsatg.annotation.Service;
import org.marsatg.http.Response;
import org.marsatg.poseidon.PoseidonProcessorBean;
import org.marsatg.properties.HttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@AutoConfigureAfter(HttpProperties.class)
public class BeanProxyFactory implements PoseidonProcessorBean, ApplicationContextAware { //implements PoseidonProcessorBean,BeanDefinitionRegistryPostProcessor

    private static Logger logger = LoggerFactory.getLogger(BeanProxyFactory.class);
    private static Map<String, Map<String, Method>> methodMap = new ConcurrentHashMap<>();
    private static Map<String, Object> serverApiBeanMap = new ConcurrentHashMap<>();

    public static Map<String, Map<String, Method>> getMethodMap() {return methodMap;}
    public static Map<String, Object> getServerApiBeanMap() { return serverApiBeanMap;}


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("BeanProxyFactory (自定义业务处理器) 执行初始化... ");
        Map<String, Object> poseidonServiceMap = applicationContext.getBeansWithAnnotation(Service.class);
        logger.info("PoseidonServiceMap-size:" + poseidonServiceMap.size());
        initFactory(poseidonServiceMap);
        logger.info("BeanProxyFactory init success ... ");
    }



    @Override
    public Response invoke(String consumerName, String serviceName, String methodName, Object... args) {
        Map<String, Method> serviceMethodMap = methodMap.get(serviceName);
        if (serviceMethodMap == null || serviceMethodMap.size() == 0) {
            String msg = "{BeanProxyFactory} consumer:" + consumerName + "-> 未找到服务: " + serviceName;
            logger.info(msg);
            return Response.getResponse(msg);
        }
        Method method = serviceMethodMap.get(methodName);
        if (method == null) {
            String msg = "{BeanProxyFactory} consumer:" + consumerName + "-> 未找到方法：" + methodName;
            logger.info(msg);
            return Response.getResponse(msg);
        }
        Object callApi = serverApiBeanMap.get(serviceName);
        Object result = null;
        //RpcTask rpcTask = new RpcTask(method,rpcApi,args);
        //service.submit(rpcTask);
        try {
            result = method.invoke(callApi, args);
        } catch (IllegalAccessException e) {
            logger.info("{BeanProxyFactory} -> IllegalAccessException：" + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.info("{BeanProxyFactory} -> InvocationTargetException：" + e.getMessage());
        }
        String loggerArgs = "[]";
        if (args != null) {
            loggerArgs = Arrays.asList(args).toString();
        }
        logger.info("{BeanProxyFactory} consumer:" + consumerName + " -> invoke(success) -> 参数 -> " + loggerArgs);
        return Response.getResponse(result);
    }


    private static void initFactory(Map<String, Object> serverApiMap) {
        Set<String> serviceNames = serverApiMap.keySet();
        for (String serviceName : serviceNames) {
            Object poseidonService = serverApiMap.get(serviceName);
            Service serviceAnnotation = poseidonService.getClass().getAnnotation(Service.class);
            if (serviceAnnotation != null && StringUtils.isNotBlank(serviceAnnotation.value())) {
                serviceName = serviceAnnotation.value();
            } else {
                logger.warn(" " + poseidonService.getClass() + "注解@Service 值为空：将使用默认服务名称：" + serviceName);
            }
            Map<String, Method> serviceMethodmap = methodMap.get(serviceName);
            if (serviceMethodmap != null) {
                logger.error("\n\n===========================================================");
                logger.error(" =================>>  发现了多个相同的相同的 @Service --->> serviceName:" + serviceName + " <<================= ");
                logger.error("===========================================================\n\n");
                System.exit(1);
            }
            serviceMethodmap = PoseidonServiceDefinition.getMethodMap(poseidonService.getClass());
            methodMap.put(serviceName, serviceMethodmap);
            serverApiBeanMap.put(serviceName, poseidonService);
            logger.info("BeanProxyFactory 服务：" + serviceName + "  -> " + serviceMethodmap);
        }
        logger.info("BeanProxyFactory 初始化完成...  ");
    }




    class AsyncTask implements Callable {

        private Method method;

        private Object result;

        private PoseidonServiceDefinition rpcApi;

        private Object[] args;

        public AsyncTask(Method method, PoseidonServiceDefinition rpcApi, Object[] args) {
            this.method = method;
            this.rpcApi = rpcApi;
            this.args = args;
        }

        @Override
        public Object call() {
            try {
                result = method.invoke(rpcApi, args);
            } catch (IllegalAccessException e) {
                logger.info(" BeanProxyFactory -> IllegalAccessException：" + e.getMessage());
            } catch (InvocationTargetException e) {
                logger.info(" BeanProxyFactory -> InvocationTargetException：" + e.getMessage());
            }
            return result;
        }
    }


}
