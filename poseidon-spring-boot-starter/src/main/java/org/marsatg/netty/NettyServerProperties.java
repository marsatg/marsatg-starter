package org.marsatg.netty;


import org.marsatg.annotation.ConfigField;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "netty-local.server")
@Component
public class NettyServerProperties {

    @ConfigField(value = "application",description = " Netty-server Name")
    private String name = "application";

    @ConfigField(value = "localhost",description = " Netty-server Host")
    private String host = "localhost";

    @ConfigField(value = "10099",description = " Netty-server Port")
    private Integer port = 10099;

    @ConfigField(value = "5000",description = " 服务器断线重连间隔(毫秒)")
    private Long reConnectTnterval = 5000L;

    @ConfigField(value = "60",description = " 服务器断线重连最大次数 -> 超时时间(重连间隔时间*次数)")
    private Integer maxReConnectTimes = 60;// 默认最大300秒（5分钟）

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getReConnectTnterval() {
        return reConnectTnterval;
    }

    public void setReConnectTnterval(Long reConnectTnterval) {
        if(reConnectTnterval<5000){
            // 最小时间不少于5秒
            reConnectTnterval = 5000l;
        }
        this.reConnectTnterval = reConnectTnterval;
    }

    public Integer getMaxReConnectTimes() {
        return maxReConnectTimes;
    }

    public void setMaxReConnectTimes(Integer maxReConnectTimes) {
        if(maxReConnectTimes<12){
            // 最少次数不少于12次
            maxReConnectTimes = 12;
        }
        this.maxReConnectTimes = maxReConnectTimes;
    }
}
