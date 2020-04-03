package org.marsatg.proxy;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.NodeMethod;
import org.marsatg.poseidon.PoseidonServiceDefinition;
import org.marsatg.annotation.Service;
import org.marsatg.properties.HttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;


@Configuration
@AutoConfigureAfter(HttpProperties.class)
@Import({UrlExecutorImpl.class,NameExecutorImpl.class})
public class BeanProxyFactory implements  ApplicationContextAware, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(BeanProxyFactory.class);
    private static Map<String, Map<String, NodeMethod>> methodMap = new ConcurrentHashMap<>();
    private static Map<String, Object> serverApiBeanMap = new ConcurrentHashMap<>();
    private static Map<String,NodeMethod> urlMap = new ConcurrentHashMap<>();
    public static Map<String, Map<String, NodeMethod>> getMethodMap() {
        return methodMap;
    }
    private static ApplicationContext context;


    public static void setUrlMap(String url,NodeMethod nodeMethod){
        Assert.state(urlMap.get(url) == null,"出现了重复的url规则");
        urlMap.put(url,nodeMethod);
    }

    public static Map<String, Object> getServerApiBeanMap() {
        return serverApiBeanMap;
    }

    public static NodeMethod getNodeMethod(String url) {
        return urlMap.get(url);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void afterPropertiesSet(){
        logger.info("BeanProxyFactory (自定义业务处理器) 执行初始化... ");
        Map<String, Object> poseidonServiceMap = context.getBeansWithAnnotation(Service.class);
        logger.info("PoseidonServiceMap-size:" + poseidonServiceMap.size());
        initFactory(poseidonServiceMap);
        logger.info("BeanProxyFactory init success ... ");
    }



    private static void initFactory(Map<String, Object> serverApiMap) {
        Set<String> serviceNames = serverApiMap.keySet();
        for (String serviceName : serviceNames) {
            Object api = serverApiMap.get(serviceName);
            Class apiClass = api.getClass();
            Service serviceAnnotation = api.getClass().getAnnotation(Service.class);
            if (serviceAnnotation != null && StringUtils.isNotBlank(serviceAnnotation.value())) {
                serviceName = serviceAnnotation.value();
            } else {
                logger.warn(" " +apiClass + "注解org.marsatg.annotation@Service 值为空：将使用默认服务名称：" + serviceName);
            }
            Map<String, NodeMethod> serviceMethodmap = methodMap.get(serviceName);
            if (serviceMethodmap != null) {
                logger.error("\n\n===========================================================");
                logger.error(" =================>>  发现了多个相同的相同的 @Service --->> serviceName:" + serviceName + " <<================= ");
                logger.error("===========================================================\n\n");
                System.exit(1);
            }
            serviceMethodmap = PoseidonServiceDefinition.getMethodMap(api,apiClass);
            methodMap.put(serviceName, serviceMethodmap);
            serverApiBeanMap.put(serviceName, api);
            logger.info("BeanProxyFactory 服务：" + serviceName + "  -> " + serviceMethodmap.size());
        }
        logger.info("BeanProxyFactory 初始化完成...  ");
    }



}
