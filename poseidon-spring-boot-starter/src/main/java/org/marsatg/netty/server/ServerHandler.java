package org.marsatg.netty.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.*;
import org.marsatg.executors.NameExecutor;
import org.marsatg.executors.UrlExecutor;
import org.marsatg.http.ResponseHolder;
import org.marsatg.http.WebManageConstants;
import org.marsatg.netty.client.ClientInfo;
import org.marsatg.proxy.BeanProxyFactory;
import org.marsatg.netty.NettyAccept;
import org.marsatg.netty.NettyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


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
    private static UrlExecutor urlExecutor;
    private static NameExecutor nameExecutor;
    private static String applicationName = "";
    private NettyProperties properties;
    private static Map<String, ClientInfo> clientMap = new ConcurrentHashMap<>();
    private static Map<Integer, Long> callCountMap = new ConcurrentHashMap<>();
    private static ExecutorService service = null;


    public static Map<String, ClientInfo> getClientMap() {
        return clientMap;
    }

    public static Map<Integer, Long> getCallCountMap() {
        return callCountMap;
    }

    public static Long getClientCallCount(int clientChannelHash) {
        return callCountMap.get(clientChannelHash);
    }

    public static void initHandlerExecutorService(int threads, boolean isFixedThreadPool) {
        if (service != null) {
            return;
        }
        if (isFixedThreadPool) {
            service = Executors.newFixedThreadPool(threads);
            logger.info("NettyServer -> FixedThreadPool -> threads:" + threads);
        } else {
            service = Executors.newCachedThreadPool();
            logger.info("NettyServer -> CachedThreadPool");
        }
    }

    @Autowired
    ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        proxyFactory = context.getBean(BeanProxyFactory.class);
        urlExecutor = context.getBean(UrlExecutor.class);
        nameExecutor = context.getBean(NameExecutor.class);
    }


    public ServerHandler(NettyProperties properties) throws Exception {
        this.properties = properties;
        applicationName = this.properties.getApplicationName();
    }

    public ServerHandler() {
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        service.submit(() -> handlerRequest(ctx, message));
    }


    private static void handlerRequest(ChannelHandlerContext ctx, Object message) {
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
        if (accept.isBlock()) {
            // TODO 如果是阻塞请求，执行完返回结果
            ResponseHolder responseHolder;
            if (accept.isUseUrl()) {
                responseHolder = urlExecutor.invoke(accept.getUrl(), args);
            } else {
                responseHolder = nameExecutor.invoke(channel.remoteAddress().toString(), serviceName, methodName, args);
            }
            accept.setData(responseHolder.getData());
            accept.setState(responseHolder.getState());
            ctx.writeAndFlush(accept.getByteBuf());
        } else {
            // TODO 如果是非阻塞请求，执行完不返回结果
            if (accept.isUseUrl()) {
                urlExecutor.invoke(accept.getUrl(), args);
            } else {
                nameExecutor.invoke(channel.remoteAddress().toString(), serviceName, methodName, args);
            }
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
            callCountMap.put(hashCode, count);
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
                int num = 1;
                String suffixName = "";
                while (true) {
                    String perfixKey = clientname + "(" + num + ")";
                    if (startsWith(perfixKey)) num++;
                    suffixName = perfixKey + ctx.channel().remoteAddress().toString();
                    clientInfo = clientMap.get(suffixName);
                    if (clientInfo == null) {
                        ctx.writeAndFlush("当前已存在：" + clientname + " 的客户端，此连接将会使用" + suffixName + "登记到服务页面");
                        clientname = suffixName;
                        break;
                    }
                }
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
            callCountMap.put(channelHash, 0L);
            clientMap.put(clientname, client);
        }
    }

    private static boolean startsWith(String perfixKey) {
        Set<String> keys = clientMap.keySet();
        for (String k : keys) {
            if (k.startsWith(perfixKey)) return true;
        }
        return false;
    }

    // TODO  移除客户端连接
    private void removeClient(String host) {
        Set<String> clientNames = clientMap.keySet();
        for (String name : clientNames) {
            ClientInfo ci = clientMap.get(name);
            if (ci == null) continue;
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
            handlerRequest(ctx, msg);
        }
    }
}
