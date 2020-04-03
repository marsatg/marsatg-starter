package org.marsatg.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.marsatg.ExceptionUtils;
import org.marsatg.UrlUtils;
import org.marsatg.netty.client.ClientHandler;
import org.marsatg.netty.factory.ClientFactory;
import org.springframework.util.Assert;


import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class NettyRequest {

    private static Long blockTimeOut = null;

    private long timeOut;

    private Object result;

    private boolean block = true;

    private boolean useUrl = false;

    private int state = 200;

    public Object getResult() {
        if (state != 200) {
            String message = this.result == null ? "error state :"+state : result.toString();
            throw new RuntimeException(message);
        }
        return result;
    }

    public <T> T getResult(Class<T> clazz) {
        if (state != 200)
            throw new RuntimeException(String.valueOf(this.result));
        if (this.result == null)
            return null;
        return this.result instanceof String ? JSON.parseObject((String) this.result, clazz) : JSON.parseObject(JSON.toJSONString(this.result), clazz);
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public static void setBlockTimeOut(Integer blockTimeOut) {
        NettyRequest.blockTimeOut = blockTimeOut.longValue();
    }

    public NettyRequest(long timeOut) {
        this.timeOut = timeOut;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private NettyRequest() {
    }

    private NettyAccept getNettyAccept(String serviceName, String methodName, Object... args) {
        NettyAccept nettyAccept = new NettyAccept();
        nettyAccept.setServiceName(serviceName);
        nettyAccept.setMethodName(methodName);
        nettyAccept.setArgs(args);
        nettyAccept.setBlock(block);
        return nettyAccept;
    }


    private NettyAccept getUrlNettyAccept(String url, Object... args) {
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        NettyAccept nettyAccept = new NettyAccept();
        nettyAccept.setUseUrl(true);
        nettyAccept.setUrl(url);
        nettyAccept.setArgs(args);
        nettyAccept.setBlock(block);
        return nettyAccept;
    }


    @Deprecated
    public static NettyRequest invokeBlock(String serverName, String serviceName, String methodName, Object... args) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.invokeBlockRequest(serverName, serviceName, methodName, args);
        return nettyRequest;
    }


    @Deprecated
    public void invokeBlockTimeout(String serverName, String serviceName, String methodName, Object... args) {
        this.invokeBlockRequest(serverName, serviceName, methodName, args);
    }


    // TODO URL-> Mapping
    public void invokeUrlBlockTimeout(String serverName, String url, Object... args) {
        ChannelHandlerContext context = this.ensureActive(serverName);
        NettyAccept accept = this.getUrlNettyAccept(url, args);
        Thread thisThread = Thread.currentThread();
        int hash = thisThread.hashCode();
        accept.setHash(hash);
        ChannelContext.logger.info("NettyRequest：" + url + " 执行请求：hash：" + hash + " -> thread:" + thisThread.getName());
        ByteBuf byteBuf = accept.getByteBuf();
        try {

            ChannelContext.saveCallServerCount(serverName);
            ClientHandler.setLockThread(hash, this, thisThread);
            // TODO 服务器返回结果之前，会一直阻塞，直到超过最大超时时间
            long ts = this.timeOut != 0 ? this.timeOut : blockTimeOut;
            long deadLine = System.currentTimeMillis() + ts;
            context.writeAndFlush(byteBuf).sync();
            LockSupport.parkUntil(thisThread, deadLine);
            if (System.currentTimeMillis() >= deadLine) {
                // TODO 如果超时，清除请求线程信息
                ClientHandler.removeLockThread(hash);
                this.result = "NettyRequest invoke timeout ... ";
                this.state = 501;
            }
        } catch (Exception e) {
            ChannelContext.logger.error("Exception in invokeBlockRequest :" + e.getClass().getName() + " -> " + e.getMessage());
            ClientHandler.removeLockThread(hash);
            this.result = "NettyRequest invoke Exception : " + e.getMessage();
            this.state = 1000;
            ExceptionUtils.printStackTrace(e);
        }
    }


    @Deprecated
    public static void invokeNonBlock(String serverName, String serviceName, String methodName, Object... args) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.block = false;
        nettyRequest.invokeNonBlockRequest(serverName, serviceName, methodName, args);
    }


    // TODO NAME -> MAPPING
    private void invokeBlockRequest(String serverName, String serviceName, String methodName, Object... args) {
        ChannelHandlerContext context = this.ensureActive(serverName);
        NettyAccept accept = this.getNettyAccept(serviceName, methodName, args);
        Thread thisThread = Thread.currentThread();
        int hash = thisThread.hashCode();
        accept.setHash(hash);
        ChannelContext.logger.info("NettyRequest：执行请求：hash：" + hash + " -> thread:" + thisThread.getName());
        ByteBuf byteBuf = accept.getByteBuf();
        try {
            ChannelContext.saveCallServerCount(serverName);
            ClientHandler.setLockThread(hash, this, thisThread);
            // TODO 服务器返回结果之前，会一直阻塞，直到超过最大超时时间
            long ts = this.timeOut != 0 ? this.timeOut : blockTimeOut;
            long deadLine = System.currentTimeMillis() + ts;
            context.writeAndFlush(byteBuf).sync();
            LockSupport.parkUntil(thisThread, deadLine);
            if (System.currentTimeMillis() >= deadLine) {
                // TODO 如果超时，清除请求线程信息
                ClientHandler.removeLockThread(hash);
                this.result = "NettyRequest invoke timeout ... ";
                this.state = 501;
            }
        } catch (Exception e) {
            ClientHandler.removeLockThread(hash);
            ChannelContext.logger.error("Exception in invokeBlockRequest :" + e.getClass().getName() + " -> " + e.getMessage());
            this.result = "NettyRequest invoke Exception : " + e.getMessage();
            this.state = 1000;
            ExceptionUtils.printStackTrace(e);
        }

    }



    private void invokeNonBlockRequest(String serverName, String serviceName, String methodName, Object... args) {
        ChannelHandlerContext context = this.ensureActive(serverName);
        NettyAccept accept = this.getNettyAccept(serviceName, methodName, args);
        ByteBuf byteBuf = accept.getByteBuf();
        ChannelContext.logger.info("NettyRequest：执行请求：(非阻塞)");
        try {
            context.writeAndFlush(byteBuf).sync();
        } catch (Exception e) {
            ChannelContext.logger.error("Exception in invokeNonBlockRequest :" + e.getClass().getName() + " -> " + e.getMessage());
            ExceptionUtils.printStackTrace(e);
        }
        ChannelContext.saveCallServerCount(serverName);
    }


    private ChannelHandlerContext ensureActive(String serverName) {
        ChannelHandlerContext context = ChannelContext.getContextMap().get(serverName);
        boolean failConnect = (context == null) || (!context.channel().isActive());
        if (failConnect) {
            NettyProperties nettyProperties = ClientFactory.getNettyProperties();
            Assert.notNull(nettyProperties, "NettyProperties is null");
            NettyServerProperties serverPropertis = this.findServerPropertis(nettyProperties, serverName);
            if (serverPropertis == null) {
                String message = serverName + " 已断开连接，未找到对应的NettyServerProperties配置";
                ChannelContext.logger.info(message);
                throw new IllegalStateException(message);
            }
            if (Reconnect.manualConnect(nettyProperties, serverPropertis)) {
                ChannelContext.logger.info(serverName + " 已重新建立连接");
                context = ChannelContext.getContextMap().get(serverName);
                return context;
            } else {
                ChannelContext.logger.error(serverName + " 无法建立连接，请检查服务端：" + JSON.toJSONString(serverPropertis));
                String message = "无法建立netty服务连接，请检查配置文件与启动参数profile";
                ChannelContext.logger.error(message);
                throw new IllegalStateException(message);
            }
        } else {
            return context;
        }

    }

    private NettyServerProperties findServerPropertis(NettyProperties properties, String serverName) {
        List<NettyServerProperties> server = properties.getServer();
        for (NettyServerProperties ser : server) {
            if (ser.getName().equals(serverName)) {
                return ser;
            }
        }
        return null;
    }


}
