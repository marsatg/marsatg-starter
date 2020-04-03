package org.marsatg.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.marsatg.netty.ChannelContext;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.NettyServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.PhantomReference;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;


public class NettyClient {

    private static volatile Thread manualConnectThreadListener= null;
    private static volatile Thread autoConnectThreadListener = null;
    private static Lock manualLock = new ReentrantLock();
    private static Lock autoLock = new ReentrantLock();
    private boolean connect = false;
    private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
    public static ExecutorService service = Executors.newFixedThreadPool(1);
    private static ThreadLocal<Integer> timesLocal = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return 1;
        }
    };
    public static final Long timeout = 3000L;


    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public void initNettyClient(NettyProperties client, NettyServerProperties server,boolean manual) {
        if(client == null){
            client = NettyProperties.getDefaultNettyProperties();
        }
        if(server == null){
            server = client.getServer().get(0);
        }
        // TODO 1 配置服务端参数
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ClientInitializer(client,server));

        ManualListener manualListener = null;
        AutoListener autoListener = null;
        if(manual){
            // TODO 2 手动模式->开启监听超时释放线程
            manualListener = new ManualListener(Thread.currentThread(),this,server.getName());
            service.submit(manualListener);
        }else{
            // TODO 2 自动模式->开启监听超时释放线程
            autoListener = new AutoListener(Thread.currentThread(),this,server.getName());
            service.submit(autoListener);
        }
        // TODO 3 开启连接
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(server.getHost(), server.getPort()).sync();
        }catch (Exception e){
            logger.info("NettyClient (连接异常) connect to "+server.getName()+"("+server.getHost()+":"+server.getPort()+") exception ...");
            if(manual){
                LockSupport.unpark(manualConnectThreadListener);
            }else {
                LockSupport.unpark(autoConnectThreadListener);
            }
            return;
        }
        logger.info("NettyClient (连接成功) connect to "+server.getName()+"("+server.getHost()+":"+server.getPort()+") success ...");
        future.channel().writeAndFlush("&clientname="+client.getApplicationName());
        // TODO 4 确认连接成功
        connect = true;
        if(manual){
            LockSupport.unpark(manualConnectThreadListener);
        }else {
            LockSupport.unpark(autoConnectThreadListener);
        }
        // TODO 5 等待关闭
        service.submit(new OpenClientChannel(future));
        logger.info("initNettyClient 执行完毕");
    }





    private static class OpenClientChannel implements Runnable{
        private ChannelFuture future;
        public OpenClientChannel(ChannelFuture future) {
            this.future = future;
        }
        @Override
        public void run() {
            try {
                future.channel().closeFuture().sync();
                logger.info("客户端关闭");
                boolean active = future.channel().isActive();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    // TODO 手动连接监听线程
    private static class ManualListener implements Runnable{
        private Thread thread;
        private NettyClient nettyClient;
        private String serverName;

        public ManualListener(Thread thread, NettyClient nettyClient, String serverName) {
            this.thread = thread;
            this.nettyClient = nettyClient;
            this.serverName = serverName;
        }

        @Override
        public void run() {
            manualLock.lock();
            manualConnectThreadListener = Thread.currentThread();
            manualLock.unlock();
            LockSupport.parkUntil(manualConnectThreadListener,System.currentTimeMillis()+timeout);
            if(!nettyClient.isConnect()){
                // todo 如果最大超出最大超时时间，中断主连接线程
                thread.interrupt();
                logger.error("NettyClient 手动连接失败，已中断当前连接线程");
            }else {
                logger.error("NettyClient 手动连接成功");
            }
            manualLock.lock();
            manualConnectThreadListener = null;
            manualLock.unlock();
        }
    }



    // TODO 自动连接监听线程
    private static class AutoListener implements Runnable{
        private Thread thread;
        private NettyClient nettyClient;
        private String serverName;
        public AutoListener(Thread thread, NettyClient nettyClient,String serverName) {
            this.thread = thread;
            this.nettyClient = nettyClient;
            this.serverName = serverName;
        }



        @Override
        public void run() {
            autoLock.lock();
            autoConnectThreadListener = Thread.currentThread();
            autoLock.unlock();
            LockSupport.parkUntil(autoConnectThreadListener,System.currentTimeMillis()+timeout);
            if(!nettyClient.isConnect()){
                thread.interrupt();
                logger.error("NettyClient 第"+timesLocal.get()+"次自动连接失败，已中断当前连接线程");
                int i = timesLocal.get() + 1;
                timesLocal.set(i);
            }else {
                timesLocal.set(1);
            }
            autoLock.lock();
            autoConnectThreadListener = null;
            autoLock.unlock();
        }
    }



}
