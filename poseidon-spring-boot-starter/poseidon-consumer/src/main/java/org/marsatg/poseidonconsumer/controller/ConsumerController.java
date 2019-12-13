package org.marsatg.poseidonconsumer.controller;

import com.alibaba.fastjson.JSON;
import org.marsatg.http.Response;
import org.marsatg.netty.ChannelContext;
import org.marsatg.netty.NettyRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {






    @RequestMapping("test2")
    @ResponseBody
    public String helloWorld2(){
        NettyRequest nettyRequest = NettyRequest.invokeBlock("nettyServer", "hello", "sayHello", "netty");
        Object result = nettyRequest.getResult();
        return JSON.toJSONString(result);
    }


    @RequestMapping("wait")
    @ResponseBody
    public String time1(){
        NettyRequest nettyRequest = NettyRequest.invokeBlock("nettyServer", "hello", "time", "等待3秒");
        Object result = nettyRequest.getResult();
        return JSON.toJSONString(result);
    }

    @RequestMapping("nowait")
    @ResponseBody
    public String time2(){
        NettyRequest nettyRequest = NettyRequest.invokeBlock("nettyServer", "hello", "time", "不用等待");
        Object result = nettyRequest.getResult();
        return JSON.toJSONString(result);
    }


    @RequestMapping("fail")
    @ResponseBody
    public String fail(){
        NettyRequest nettyRequest = NettyRequest.invokeBlock("nettyServer", "hello", "time2", "不用等待");
        Object result = nettyRequest.getResult();
        return JSON.toJSONString(result);
    }
}
