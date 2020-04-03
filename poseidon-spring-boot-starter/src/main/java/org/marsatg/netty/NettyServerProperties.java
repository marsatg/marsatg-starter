package org.marsatg.netty;


import com.alibaba.fastjson.JSON;
import org.marsatg.annotation.ConfigField;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "netty-local.server")
@Component
public class NettyServerProperties {

    @ConfigField(value = "application", description = " Netty-server Name")
    private String name = "";

    @ConfigField(value = "localhost", description = " Netty-server Host")
    private String host = "";

    @ConfigField(value = "10099", description = " Netty-server Port")
    private Integer port = null;

    @ConfigField(value = "5000", description = " 服务器断线重连间隔(毫秒)")
    private Long reConnectTnterval = 5000L;

    @ConfigField(value = "60", description = " 服务器断线重连最大次数 -> 超时时间(重连间隔时间*次数)")
    private Integer maxReConnectTimes = 2;//  默认重连2次

    private boolean useGroup = false;

    private String groupName = "";

    private int groupWeight = 1;

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
        if (reConnectTnterval < 5000) {
            // 最小时间不少于5秒
            reConnectTnterval = 5000l;
        }
        this.reConnectTnterval = reConnectTnterval;
    }

    public Integer getMaxReConnectTimes() {
        return maxReConnectTimes;
    }

    public void setMaxReConnectTimes(Integer maxReConnectTimes) {
        Assert.state(maxReConnectTimes > 0," maxReConnectTimes must be > 0 ");
        this.maxReConnectTimes = maxReConnectTimes;
    }

    public boolean isUseGroup() {
        return useGroup;
    }

    public void setUseGroup(boolean useGroup) {
        this.useGroup = useGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupWeight() {
        return groupWeight;
    }

    public void setGroupWeight(int groupWeight) {
        this.groupWeight = groupWeight;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
