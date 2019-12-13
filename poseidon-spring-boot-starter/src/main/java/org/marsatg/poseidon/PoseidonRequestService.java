package org.marsatg.poseidon;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.marsatg.http.HttpClientUtils;
import org.marsatg.http.Request;
import org.marsatg.http.Response;
import org.marsatg.http.factory.ClientTempleteFactory;
import org.marsatg.properties.HttpProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ClientTempleteFactory.class)
public class PoseidonRequestService  {


    @Autowired
    HttpProperties httpProperties;

    @Autowired
    ClientTempleteFactory clientTempleteFactory;



    public Response invoke(String serverName,String serviceName, String methodName, Object... args) {
        String url = clientTempleteFactory.getServerUrl(serverName);
        if(StringUtils.isBlank(url)){
            return Response.getResponse(" 请求的服务不存在 -> "+serverName);
        }
        Request request = new Request(httpProperties.getApplicationName(),serviceName,methodName,args);
        String response = HttpClientUtils.ajaxPostJson(url, JSON.toJSONString(request), "UTF-8");
        return JSON.parseObject(response,Response.class);
    }
}
