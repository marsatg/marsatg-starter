package org.marsatg.http.factory;


import org.apache.commons.lang3.StringUtils;
import org.marsatg.PoseidonConstants;
import org.marsatg.annotation.ClientInitException;
import org.marsatg.properties.HttpProperties;
import org.marsatg.properties.HttpServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@AutoConfigureAfter(HttpProperties.class)
public class ClientTempleteFactory {

    private static Logger logger = LoggerFactory.getLogger(ClientTempleteFactory.class);

    private static boolean lazyInitDone = false;

    @Autowired
    HttpProperties httpProperties;

    public HttpProperties getHttpProperties() {
        return httpProperties;
    }

    public void setHttpProperties(HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    private static Map<String,String> serverUrlMap = new ConcurrentHashMap<>();


    public String getServerUrl(String serverName) {
        if(!lazyInitDone){
            this.initServerURLMap();
            lazyInitDone = true;
        }
        return serverUrlMap.get(serverName);
    }




    public void initServerURLMap(){
        logger.info("Prepare Poseidon serverURL-Map init... ");
        List<HttpServerProperties> remoteServers = httpProperties.getServer();
        for(HttpServerProperties server:remoteServers){
            String name = server.getName();
            String host = server.getHost();
            Integer port = server.getPort();
            String protocol = server.getProtocol();
            String contextPath = server.getContextPath();
            if(StringUtils.isBlank(name)){
                throw new ClientInitException("poseidon.remoteServer.name 不能为空");
            }
            if(StringUtils.isBlank(host)){
                throw new ClientInitException("poseidon.remoteServer.host 不能为空");
            }
            if(port == null){
                throw new ClientInitException("poseidon.remoteServer.port 不能为空");
            }
            if(StringUtils.isNotBlank(contextPath)){
                if(!contextPath.startsWith("/") || contextPath.endsWith("/")){
                    throw new ClientInitException("poseidon.remoteServer.contextPath 必须以/开头，且不能以/结尾");
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(protocol).append("://").append(host).append(":").append(port).append(contextPath).append(PoseidonConstants.CONTROLLER_REQUEST_MAPPING).append(PoseidonConstants.ACCEPT_REQUEST_MAPPING);
            serverUrlMap.put(name,builder.toString());
        }
        logger.info("Poseidon remote-server init success size: "+serverUrlMap.size());
    }




}
