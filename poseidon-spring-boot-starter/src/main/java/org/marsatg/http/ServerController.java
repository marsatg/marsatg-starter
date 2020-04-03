package org.marsatg.http;

import org.marsatg.PoseidonConstants;
import org.marsatg.executors.NameExecutor;
import org.marsatg.proxy.BeanProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping(PoseidonConstants.CONTROLLER_REQUEST_MAPPING)
public class ServerController{

    private static Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    NameExecutor nameExecutor;



    @RequestMapping(PoseidonConstants.ACCEPT_REQUEST_MAPPING)
    @ResponseBody
    public ResponseHolder accept(@RequestBody Request request){
        String consumerName = request.getConsumerName();
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        return nameExecutor.invoke(consumerName,serviceName,methodName,args);
    }


}
