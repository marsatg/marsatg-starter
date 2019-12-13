package org.marsatg.netty.factory;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.netty.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.applet.AppletContext;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AutoConfigureAfter(NettyProperties.class)
public class ClientFactory implements InitializingBean,ApplicationContextAware {

    private static ApplicationContext context;
    private static NettyProperties properties;

    public static NettyProperties getNettyProperties() {
        return nettyProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }




    private static NettyProperties nettyProperties;



    @Override
    public void afterPropertiesSet() throws Exception {
        nettyProperties = context.getBean(NettyProperties.class);
        NettyRequest.setBlockTimeOut(nettyProperties.getRequestBlockTimeout());
        List<NettyServerProperties> nettyServerProperties = nettyProperties.getServer();
        if(nettyServerProperties == null || nettyServerProperties.size() == 0){
            throw new Exception("\r\n注解 @EnableNettyClient 至少需要配置一个Netty服务配置，例如:\r\nnetty-local.server[0].host=localhost\r\nnetty-local.server[0].port=10099\r\nnetty-local.server[0].name=nettyServer\r\n");
        }
        if(StringUtils.isBlank(nettyProperties.getApplicationName())){
            throw new Exception("\r\n注解 @EnableNettyClient 需要配置当前客户端的applicationName，例如:\r\nnetty-local.applicationName=myNettyClient\r\n");
        }
        this.hasRepeatServerName(nettyServerProperties);
        for(NettyServerProperties serverProperties:nettyServerProperties ){
            Reconnect.autoConnect(nettyProperties, serverProperties);
        }
    }

    private void hasRepeatServerName(List<NettyServerProperties> nettyServerProperties) throws Exception {
        Set<String> sets = new HashSet<>();
        for(NettyServerProperties serverProperties:nettyServerProperties ){
            String name = serverProperties.getName();
            boolean contains = sets.contains(name);
            if(contains){
                throw new Exception("NettyServer 配置不允许出现重复的serverName："+name);
            }
            sets.add(name);
        }
        sets = null;
    }



}
