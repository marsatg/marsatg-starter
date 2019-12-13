package org.marsatg.properties;


import org.marsatg.annotation.ConfigField;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "http-local.server")
@Component
public class HttpServerProperties {

    @ConfigField("远程服务名称")
    private String name = "remotePoseidonServer";

    @ConfigField("远程服务Host")
    private String host = "localhost";

    @ConfigField("远程服务端口")
    private Integer port = 8080;

    @ConfigField("远程服务协议")
    private String protocol = "static/http";

    @ConfigField("远程服务contextPath")
    private String contextPath = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

}
