package org.marsatg.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.marsatg.PoseidonConstants;
import org.marsatg.proxy.BeanProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(PoseidonConstants.CONTROLLER_WEB_MANAGE_MAPPING)
public class WebManageController {



    private static Logger logger = LoggerFactory.getLogger(WebManageController.class);

    @Autowired
    WebManageService webManageService;



    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_APPLICATION_INFO)
    @ResponseBody
    public String applicationInfo(){
        return webManageService.getApplicationInfo();
    }


    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_SERVICE_LIST)
    @ResponseBody
    public String serviceList(){
         return webManageService.getServiceList();
    }



    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_METHOD_LIST)
    @ResponseBody
    public String methodList(@RequestBody JSONObject object){
        String serviceName = object.getString("serviceName");
        return webManageService.getMethodList(serviceName);
    }


    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_CLIENT_LIST)
    @ResponseBody
    public String clientList(){
        return webManageService.getClientList();
    }


    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_SERVER_LIST)
    @ResponseBody
    public String serverList(){
        return webManageService.getServerList();
    }




    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_CONNECT_TO_SERVER)
    @ResponseBody
    public String connectServer(@RequestBody JSONObject object){
        return webManageService.connectServer(object);
    }



    @Bean
    public CorsFilter corsFilter() throws Exception {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        logger.info("init CorsFilter ... ");
        return new CorsFilter(source);
    }
}
