package org.marsatg.netty.factory;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.server.NettyServer;
import org.marsatg.netty.server.ServerHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AutoConfigureAfter(NettyProperties.class)
@Import(ServerHandler.class)
public class ServerFactory implements InitializingBean,Runnable {

    private static ExecutorService service = Executors.newSingleThreadExecutor();

    @Autowired
    NettyProperties nettyProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        String applicationName = nettyProperties.getApplicationName();
        Integer applicationPort = nettyProperties.getApplicationPort();
        if(StringUtils.isBlank(applicationName) || applicationPort==null){
            throw new Exception("\r\n注解 @EnableNettyServer 必须配置当前netty服务的名称和端口，例如：\r\nnetty-local.applicationName=myServer\r\nnetty-local.applicationPort=8081\r\n");
        }
        service.submit(this);
    }

    @Override
    public void run() {
        NettyServer nettyServer = new NettyServer();
        try {
            nettyServer.initNettyServer(nettyProperties);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
