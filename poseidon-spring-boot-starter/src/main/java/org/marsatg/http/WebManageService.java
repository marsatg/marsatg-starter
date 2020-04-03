package org.marsatg.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import javafx.beans.binding.MapBinding;
import org.apache.commons.lang3.StringUtils;
import org.marsatg.annotation.EnableWebManage;
import org.marsatg.annotation.Method;
import org.marsatg.annotation.Service;
import org.marsatg.netty.ChannelContext;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.NettyServerProperties;
import org.marsatg.netty.Reconnect;
import org.marsatg.netty.client.ClientHandler;
import org.marsatg.netty.client.ClientInfo;
import org.marsatg.netty.server.ServerHandler;
import org.marsatg.proxy.BeanProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WebManageService {


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ExecutorService service = Executors.newFixedThreadPool(1);
    @Autowired
    private NettyProperties properties;



    public String getApplicationInfo(){
        JSONObject object = new JSONObject();
        object.put("applicationName",properties.getApplicationName());
        object.put("applicationPort",properties.getApplicationPort());
        Map<Integer, Long> callCountMap = ServerHandler.getCallCountMap();
        Set<Integer> hash = callCountMap.keySet();
        Long total = 0L;
        for(Integer h:hash){
            total += callCountMap.get(h);
        }
        object.put("totalCount",total);
        return object.toJSONString();
    }



    //TODO 服务列表
    public String getServiceList(){
        Map<String, Object> map = BeanProxyFactory.getServerApiBeanMap();
        List<JSONObject> list = new ArrayList<>();
        Set<String> serverName = map.keySet();
        for(String name:serverName){
            Object o = map.get(name);
            Class<?> cls = o.getClass();
            Service an = cls.getAnnotation(Service.class);
            JSONObject object = new JSONObject();
            object.put("serviceName",an.name());
            object.put("instanceName",an.value());
            object.put("classInfo",cls.getName());
            object.put("desc",an.desc());
            int methodCount = 0;
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            for(java.lang.reflect.Method method:methods){
                if(method.getAnnotation(Method.class) != null){
                    methodCount ++;
                }
            }
            object.put("methodCount",methodCount);
            list.add(object);
        }
        return JSON.toJSONString(list);
    }




    //TODO 方法列表
    public String getMethodList(String serviceName){
        Map<String,Object> result = new HashMap<>();
        if(StringUtils.isBlank(serviceName)){
            result.put("serviceName",serviceName);
            result.put("list","[]");
            return JSON.toJSONString(result);
        }
        Map<String, Object> map = BeanProxyFactory.getServerApiBeanMap();
        Object service = map.get(serviceName);
        if(service == null){
            result.put("serviceName",serviceName);
            result.put("list","[]");
            return JSON.toJSONString(result);
        }
        Class<?> cls = service.getClass();
        java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
        List<JSONObject> list = new ArrayList<>();
        for(java.lang.reflect.Method m:methods){
            Method ax = m.getAnnotation(Method.class);
            if( ax == null){
                continue;
            }
            JSONObject object = new JSONObject();
            object.put("desc",ax.desc());
            object.put("classInfo",cls.getName());
            object.put("instanceIovokeName",ax.value());
            object.put("methodInfo",m.toString());
            object.put("async",ax.async());
            list.add(object);
        }
        result.put("serviceName",serviceName);
        result.put("list",list);
        return JSON.toJSONString(result);
    }



    //TODO 客户端列表
    public String getClientList(){
        Map<String, ClientInfo> clientMap = ServerHandler.getClientMap();
        Set<String> names = clientMap.keySet();
        List<JSONObject> list = new ArrayList<>();
        for(String name:names){
            ClientInfo ci = clientMap.get(name);
            JSONObject object = new JSONObject();
            object.put("registerName",ci.getRegisterName());
            object.put("clientHost",ci.getClientHost());
            object.put("clientPort",ci.getClientPort());
            object.put("startTime",sdf.format(ci.getStartTime()));
            object.put("clientCallCount",ServerHandler.getClientCallCount(ci.getChannelHash()));
            list.add(object);
        }
        return JSON.toJSONString(list);
    }


    //TODO 服务端列表
    public String getServerList(){
        JSONObject result = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        Map<String, ChannelHandlerContext> contextMap = ChannelContext.getContextMap();
        Set<String> serverNames = contextMap.keySet();
        for(String serverName:serverNames){
            ChannelHandlerContext context = contextMap.get(serverName);
            InetSocketAddress addr = (InetSocketAddress)context.channel().remoteAddress();
            if(addr == null){
                continue;
            }
            JSONObject object = new JSONObject();
            object.put("serverHost",addr.getHostName());
            object.put("serverPort",addr.getPort());
            object.put("serverName",serverName);
            object.put("serverCallCount",ChannelContext.getcallServerCountMap().get(serverName));
            object.put("serverStatus",context.channel().isActive());
            list.add(object);
        }
        result.put("list",list);
        result.put("blockThreadCount",ClientHandler.getThreadMap().size());
        result.put("blockRequestCount",ClientHandler.getRequestMap().size());
        result.put("blockRequestMap",ClientHandler.getRequestMap());
        result.put("blockThreadMap",ClientHandler.getThreadMap());
        result.put("counter",ClientHandler.counter.get());
        result.put("contextMap",contextMap.size());
        return result.toJSONString();
    }



    private static boolean lock = false;

    public String connectServer(JSONObject object){
        if(lock){
            return "有正在连接的任务未处理完";
        }
        String serverName = object.getString("name");
        String host = object.getString("host");
        Integer port = object.getInteger("port");
        if(StringUtils.isBlank(serverName)){
            return "请求name为空";
        }
        ChannelHandlerContext context = ChannelContext.getContextMap().get(serverName);
        if(context.channel().isActive()){
            return "当前已处于连接状态";
        }
        NettyServerProperties server = new NettyServerProperties();
        server.setHost(host);
        server.setName(serverName);
        server.setPort(port);
        boolean connect = false;
        lock = true;
        connect = Reconnect.manualConnect(properties,server);
        lock = false;
        return connect?"连接成功":"连接失败";
    }




}
