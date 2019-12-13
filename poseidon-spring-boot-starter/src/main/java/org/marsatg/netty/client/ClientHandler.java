package org.marsatg.netty.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.marsatg.netty.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler extends SimpleChannelInboundHandler {


    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static Lock lock = new ReentrantLock();
    private static Map<Integer, NettyRequest> requestMap = new ConcurrentHashMap<>();
    private static Map<Integer, Thread> threadMap = new ConcurrentHashMap<>();
    private NettyServerProperties nettyServerProperties;
    private NettyProperties nettyProperties;
    public ClientHandler(){}



    // TODO 构造器
    public ClientHandler(NettyProperties properties,NettyServerProperties nettyServerProperties){
        this.nettyProperties = properties;
        this.nettyServerProperties = nettyServerProperties;
    }

    // TODO 设置回调对象
    public static void setLockThread(Integer hash, NettyRequest nettyRequest, Thread thread){
        try {
            requestMap.put(hash, nettyRequest);
            threadMap.put(hash, thread);
        }catch (Exception e){
            logger.info(" Exception in ClientHandler.setLockThread("+hash+","+nettyRequest+","+thread+") \r\n exception message："+e.getMessage());
            LockSupport.unpark(thread);
            requestMap.remove(hash);
            threadMap.remove(hash);
        }
    }

    // TODO 清除回调对象
    public static void removeLockThread(Integer hash){
        requestMap.remove(hash);
        threadMap.remove(hash);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelContext.initChannelContext(nettyServerProperties.getName(),ctx);
        super.channelActive(ctx);
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        NettyAccept accept = null;
        try {
            accept = JSON.parseObject(o.toString(), NettyAccept.class);
        }catch (Exception e){
            this.processPraseException(o);
            return;
        }
        Integer hashCode = accept.getHash();
        logger.info("NettyClient：完成请求：hash："+hashCode);
        // TODO 1 设置服务端响应
        NettyRequest nettyRequest = requestMap.get(hashCode);
        if(nettyRequest == null){
            // TODO nettyRequest 已经由请求端超时自动清除
            return;
        }
        nettyRequest.setResult(accept.getData());
        // TODO 2 唤醒请求线程
        Thread thread = threadMap.get(hashCode);
        LockSupport.unpark(thread);
        // TODO 3 清除请求线程
        removeLockThread(hashCode);
    }






    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String message = cause.getMessage();
        logger.error("NettyClient 捕获到异常："+ message);
        if(message.contains("远程主机强迫关闭")){
            // TODO 如果连接断开->启动重连机制
            InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            String host = socketAddress.getAddress().toString().split("/")[1];
            int port = socketAddress.getPort();
            NettyServerProperties server = this.findServerPropreties(host, port);
            Reconnect.autoConnect(nettyProperties, server);
        }
    }





    public  NettyServerProperties findServerPropreties(String host,Integer port)  {
        // host 是一个绝对ip地址
        List<NettyServerProperties> servers = nettyProperties.getServer();
        for(NettyServerProperties s:servers){
            String serverhost = s.getHost();
            serverhost = serverhost.equals("localhost")?"127.0.0.1":serverhost;
            if(s.getPort().equals(port) && serverhost.equals(host)){
                return s;
            }
        }
        logger.warn("服务端列表："+JSON.toJSONString(servers, SerializerFeature.PrettyFormat));
        logger.error("服务端列表未找到指定配置："+host+":"+port);
        return null;
    }




    private void processPraseException(Object msg){
        logger.info(msg.toString());
    }

    public static Map<Integer, NettyRequest> getRequestMap() {
        return requestMap;
    }

    public static Map<Integer, Thread> getThreadMap() {
        return threadMap;
    }
}
