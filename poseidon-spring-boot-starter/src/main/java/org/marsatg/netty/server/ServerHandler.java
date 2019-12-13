package org.marsatg.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.marsatg.http.WebManageConstants;
import org.marsatg.netty.client.ClientInfo;
import org.marsatg.proxy.BeanProxyFactory;
import org.marsatg.http.Response;
import org.marsatg.netty.NettyAccept;
import org.marsatg.netty.NettyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.AnnotationMetadata;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerHandler extends SimpleChannelInboundHandler implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private static BeanProxyFactory proxyFactory;
    private static String consumerName = "";
    private NettyProperties properties;
    private static Map<String, ClientInfo> clientMap = new ConcurrentHashMap<>();
    private static Map<Integer, Long> callCountMap = new ConcurrentHashMap<>();
    private static ExecutorService service = null;


    public static Map<String, ClientInfo> getClientMap() {return clientMap;}
    public static Map<Integer, Long> getCallCountMap() { return callCountMap; }
    public static Long getClientCallCount(int clientChannelHash) { return callCountMap.get(clientChannelHash);}
    public static void initHandlerExecutorService(int threads,boolean isFixedThreadPool){
        if(service != null){
            return;
        }
        if(isFixedThreadPool){
            service = Executors.newFixedThreadPool(threads);
            logger.info("NettyServer -> FixedThreadPool -> threads:"+threads);
        }else {
            service = Executors.newCachedThreadPool();
            logger.info("NettyServer -> CachedThreadPool");
        }
    }

    @Autowired
    ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        proxyFactory = context.getBean(BeanProxyFactory.class);
    }



    public ServerHandler(NettyProperties properties) {
        this.properties = properties;
        if (this.properties == null) {
            this.properties = new NettyProperties();
            this.properties.setApplicationName("netty-server");
            this.properties.setApplicationPort(10079);
        }
        consumerName = this.properties.getApplicationName();
    }

    public ServerHandler() {
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        boolean b = ctx.channel().hasAttr(AttributeKey.valueOf("123"));
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        service.submit(new ServerExecutorTask(ctx,message));
    }


    private static void handlerRequest(ChannelHandlerContext ctx,Object message){
        NettyAccept accept = null;
        try {
            accept = JSON.parseObject(message.toString(), NettyAccept.class);
        } catch (Exception e) {
            processParseException(ctx, message);
            return;
        }
        logger.info("NettyServer 收到请求：hash: " + accept.getHash());
        Object[] args = accept.getArgs();
        String serviceName = accept.getServiceName();
        String methodName = accept.getMethodName();
        Channel channel = ctx.channel();
        if(accept.isBlock()){
            // TODO 如果是阻塞请求，执行完返回结果
            Response response = proxyFactory.invoke(channel.remoteAddress().toString(), serviceName, methodName, args);
            accept.setData(response);
            ctx.writeAndFlush(accept.getByteBuf());
        }else {
            // TODO 如果是非阻塞请求，执行完不返回结果
            proxyFactory.invoke(channel.remoteAddress().toString(), serviceName, methodName, args);
        }
        count(channel);
    }



    // TODO  客户端 调用统计
    private static void count(Channel channel) {
        // TODO 如果注解启用了客户端的调用统计
        if (WebManageConstants.isCountClientCall()) {
            int hashCode = channel.hashCode();
            Long count = callCountMap.get(hashCode);
            count += 1;
            callCountMap.put(hashCode,count);
        }
    }



    // TODO  异常捕获
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.info("NettyServer 异常：:(客户端：" + ctx.channel().remoteAddress() + ")已关闭连接");
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        String host = socketAddress.toString();
        this.removeClient(host);
        super.channelUnregistered(ctx);
    }


    // TODO  转换异常处理
    private static void processParseException(ChannelHandlerContext ctx, Object message) {
        String msg = message.toString();
        if (msg.startsWith("&clientname=")) {
            String clientname = msg.split("=")[1];
            ClientInfo clientInfo = clientMap.get(clientname);
            if (clientInfo != null) {
                ctx.writeAndFlush("当前已存在：" + clientname + " 的客户端，此连接将不会登记到服务页面，但不会影响当前客户端的连接与使用");
                return;
            }
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            int channelHash = ctx.channel().hashCode();
            ClientInfo client = new ClientInfo();
            client.setRegisterName(clientname);
            client.setStartTime(new Date());
            client.setClientHost(address.toString());
            client.setClientPort(address.getPort());
            client.setChannelHash(channelHash);
            logger.info("客户端：" + clientname + " 已加入 ->" + client.getClientHost());
            callCountMap.put(channelHash,0L);
            clientMap.put(clientname, client);
        }
    }



    // TODO  移除客户端连接
    private void removeClient(String host) {
        Set<String> clientNames = clientMap.keySet();
        for (String name : clientNames) {
            ClientInfo ci = clientMap.get(name);
            if (ci.getClientHost().equals(host)) {
                clientMap.remove(name);
                callCountMap.remove(ci.getChannelHash());
                logger.error("客户端：" + ci.getRegisterName() + " 已移除 ->" + ci.getClientHost());
                break;
            }
        }
    }




    // TODO 为同一个通道
    private class ServerExecutorTask implements Runnable {

        private ChannelHandlerContext ctx;
        private Object msg;

        public ServerExecutorTask(ChannelHandlerContext ctx, Object msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        @Override
        public void run() {
            handlerRequest(ctx,msg);
        }
    }
}
