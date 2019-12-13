package org.marsatg.netty;

import org.marsatg.annotation.ConfigField;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "netty-local")
@Component
public class NettyProperties {

    @ConfigField(value = "application",description = "当前服务作为 Netty-server 时的名称")
    private String applicationName =  "";

    @ConfigField(value = "10099",description = "当前服务作为  Netty-server 时的端口")
    private Integer applicationPort = null;

    @ConfigField(value = "5",description = "服务端使用 FixedThreadPool 线程池时的最大线程数量")
    private Integer threads = 5;

    @ConfigField(value = "false",description = "服务端是否使用 FixedThreadPool 线程池(默认使用CachedThreadPool)")
    private boolean isFixedThreadPool = false;

    @ConfigField(value = "6000",description = "客户端（阻塞）调用服务端最大超时时间")
    private Integer requestBlockTimeout = 6000;


    @ConfigField(value = "",description = "当前服务作为  Netty-client 时的服务端配置")
    private List<NettyServerProperties> server = new ArrayList<>();

    public List<NettyServerProperties> getServer() {
        return server;
    }

    public void setServer(List<NettyServerProperties> server) {
        this.server = server;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Integer getApplicationPort() {
        return applicationPort;
    }

    public void setApplicationPort(Integer applicationPort) {
        this.applicationPort = applicationPort;
    }


    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getRequestBlockTimeout() {
        return requestBlockTimeout;
    }

    public void setRequestBlockTimeout(Integer requestBlockTimeout) {
        this.requestBlockTimeout = requestBlockTimeout;
    }

    public static NettyProperties getDefaultNettyProperties(){
        NettyProperties properties = new NettyProperties();
        properties.applicationName = "localhost";
        properties.applicationPort = 10079;
        NettyServerProperties server = new NettyServerProperties();
        server.setHost("localhost");
        server.setName("nettyServer");
        server.setPort(10079);
        properties.setServer(Arrays.asList(server));
        return properties;
    }

    public boolean isFixedThreadPool() {
        return isFixedThreadPool;
    }

    public void setIsFixedThreadPool(boolean fixedThreadPool) {
        isFixedThreadPool = fixedThreadPool;
    }
}
