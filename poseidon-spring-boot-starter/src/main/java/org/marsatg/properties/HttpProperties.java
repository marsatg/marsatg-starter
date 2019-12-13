package org.marsatg.properties;

import org.marsatg.annotation.ConfigField;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "http-local")
@Component
public class HttpProperties {



    @ConfigField(description = "远程服务信息配置")
    private List<HttpServerProperties> server;

    @ConfigField(description = "作为消费者时名称(applicationName)")
    private String applicationName;

    public List<HttpServerProperties> getServer() {
        return server;
    }

    public void setServer(List<HttpServerProperties> server) {
        this.server = server;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }


}
