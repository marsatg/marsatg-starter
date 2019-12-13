package org.marsatg.netty.server;


import ch.qos.logback.core.net.server.ServerListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private boolean connect = false;

    public boolean isConnect() {
        return connect;
    }

    public void initNettyServer(NettyProperties nettyProperties) throws InterruptedException {
        if(nettyProperties == null){
            nettyProperties = NettyProperties.getDefaultNettyProperties();
        }
        ServerHandler.initHandlerExecutorService(nettyProperties.getThreads(),nettyProperties.isFixedThreadPool());;
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(boss,worker).channel(NioServerSocketChannel.class).childHandler(new ServerInitializer(nettyProperties));
        NettyClient.service.submit(new ServerListener(this,nettyProperties.getApplicationPort()));
        ChannelFuture future = sb.bind(nettyProperties.getApplicationPort()).sync();
        logger.info("NettyServer started success ...");
        logger.info("NettyServer -> name:"+nettyProperties.getApplicationName()+" -> port:"+nettyProperties.getApplicationPort());
        connect = true;
        future.channel().closeFuture().sync();

    }


    private static class ServerListener implements Runnable{

        private NettyServer server;
        private Object param;

        public ServerListener(NettyServer server, Object param) {
            this.server = server;
            this.param = param;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!server.isConnect()){
                logger.error("NettyServer无法开启，请检查端口占用："+param);
                System.exit(1);
            }
        }
    }


}
