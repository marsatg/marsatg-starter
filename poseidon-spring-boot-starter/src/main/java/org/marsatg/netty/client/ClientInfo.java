package org.marsatg.netty.client;

import java.util.Date;

public class ClientInfo {

    private String registerName;

    private Date startTime;

    private Integer clientPort;

    private String clientHost;

    private Integer channelHash;

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public Integer getChannelHash() {
        return channelHash;
    }

    public void setChannelHash(Integer channelHash) {
        this.channelHash = channelHash;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ClientInfo)){
            return false;
        }
        ClientInfo c = (ClientInfo)o;
        if(c.getRegisterName().equals(this.getRegisterName())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return  registerName != null ? registerName.hashCode() : 0;
    }
}
