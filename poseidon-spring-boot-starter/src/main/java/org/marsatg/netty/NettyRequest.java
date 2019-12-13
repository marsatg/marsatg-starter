package org.marsatg.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.marsatg.netty.client.ClientHandler;
import org.marsatg.netty.factory.ClientFactory;


import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class NettyRequest {

    private static Integer blockTimeOut = null;

    private Object result;

    private boolean block = true;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public static void setBlockTimeOut(int blockTimeOut) {
        NettyRequest.blockTimeOut = blockTimeOut;
    }

    private NettyAccept getNettyAccept(String serviceName, String methodName, Object... args) {
        NettyAccept nettyAccept = new NettyAccept();
        nettyAccept.setServiceName(serviceName);
        nettyAccept.setMethodName(methodName);
        nettyAccept.setArgs(args);
        nettyAccept.setBlock(block);
        return nettyAccept;
    }


    public static NettyRequest invokeBlock(String serverName, String serviceName, String methodName, Object... args) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.invokeBlockRequest(serverName, serviceName, methodName, args);
        return nettyRequest;
    }


    public static void invokeNonBlock(String serverName, String serviceName, String methodName, Object... args) {
        NettyRequest nettyRequest = new NettyRequest();
        nettyRequest.block = false;
        nettyRequest.invokeNonBlockRequest(serverName, serviceName, methodName, args);
    }


    private void invokeBlockRequest(String serverName, String serviceName, String methodName, Object... args) {
        ChannelHandlerContext context = this.ensureActive(serverName);
        NettyAccept accept = this.getNettyAccept(serviceName, methodName, args);
        Thread thisThread = Thread.currentThread();
        int hash = thisThread.hashCode();
        accept.setHash(hash);
        ChannelContext.logger.info("NettyClient：执行请求：hash：" + hash + " -> thread:" + thisThread.getName());
        ByteBuf byteBuf = accept.getByteBuf();
        try {
            context.writeAndFlush(byteBuf).sync();
            ChannelContext.saveCallServerCount(serverName);
            ClientHandler.setLockThread(hash, this, thisThread);
            // TODO 服务器返回结果之前，会一直阻塞，直到超过最大超时时间
            long deadLine = System.currentTimeMillis() + blockTimeOut;
            LockSupport.parkUntil(this, deadLine);
            if (System.currentTimeMillis() >= deadLine) {
                // TODO 如果超时，清除请求线程信息
                ClientHandler.removeLockThread(hash);
            }
        } catch (Exception e) {
            ChannelContext.logger.error("Exception in invokeBlockRequest :" + e.getClass().getName() + " -> " + e.getMessage());
        }

    }


    private void invokeNonBlockRequest(String serverName, String serviceName, String methodName, Object... args) {
        ChannelHandlerContext context = this.ensureActive(serverName);
        NettyAccept accept = this.getNettyAccept(serviceName, methodName, args);
        ByteBuf byteBuf = accept.getByteBuf();
        ChannelContext.logger.info("NettyClient：执行请求：(非阻塞)");
        try {
            context.writeAndFlush(byteBuf).sync();
        } catch (Exception e) {
            ChannelContext.logger.error("Exception in invokeNonBlockRequest :" + e.getClass().getName() + " -> " + e.getMessage());
        }
        ChannelContext.saveCallServerCount(serverName);
    }


    private ChannelHandlerContext ensureActive(String serverName) {
        ChannelHandlerContext context = ChannelContext.getContextMap().get(serverName);
        boolean failConnect = (context == null) || (!context.channel().isActive());
        if (failConnect) {
            NettyProperties nettyProperties = ClientFactory.getNettyProperties();
            NettyServerProperties serverPropertis = this.findServerPropertis(nettyProperties, serverName);
            if (serverPropertis == null) {
                ChannelContext.logger.info(serverName + " 已断开连接，未找到对应的NettyServerProperties配置");
                return null;
            }
            if (Reconnect.manualConnect(nettyProperties, serverPropertis)) {
                ChannelContext.logger.info(serverName + " 已重新建立连接");
            } else {
                ChannelContext.logger.info(serverName + " 无法建立连接，请检查服务端：" + JSON.toJSONString(serverPropertis));
            }
            return ChannelContext.getContextMap().get(serverName);
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
