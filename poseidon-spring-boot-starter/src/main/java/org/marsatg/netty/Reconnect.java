package org.marsatg.netty;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Reconnect implements Runnable {


    private static ExecutorService startService = Executors.newCachedThreadPool();
    private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private NettyProperties nettyProperties;
    private NettyServerProperties nettyServerProperties;



    public static void autoConnect(NettyProperties nettyProperties, NettyServerProperties nettyServerProperties) {
        if(isNotEmpty(nettyServerProperties)){
            Reconnect reconnect = new Reconnect(nettyProperties, nettyServerProperties);
            startService.submit(reconnect);
        }else {
            logger.info("NettyServerProperties is empty , and not start -> "+nettyServerProperties.toString());

        }
    }

    private static boolean isNotEmpty(NettyServerProperties nettyServerProperties){
        String name = nettyServerProperties.getName();
        String host = nettyServerProperties.getHost();
        Integer port = nettyServerProperties.getPort();
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(host) && port != null;
    }

    public static boolean manualConnect(NettyProperties properties, NettyServerProperties serverProperties){
        int failTime = 0;
        Integer maxTimes = 1;
        NettyClient client = new NettyClient();
        client.initNettyClient(properties,serverProperties,true);
        return client.isConnect();
    }



    // TODO 构造函数
    private Reconnect(NettyProperties nettyProperties, NettyServerProperties nettyServerProperties) {
        this.nettyProperties = nettyProperties;
        this.nettyServerProperties = nettyServerProperties;
    }


    // TODO 自动连接Task
    @Override
    public void run() {
        if (nettyServerProperties == null ) {
            logger.info("nettyServerProperties is null , 已终止重连");
            return;
        }
        if ( nettyProperties == null ) {
            logger.info("nettyProperties is null , 已终止重连");
            return;
        }
        String name = nettyServerProperties.getName();
        String host = nettyServerProperties.getHost();
        Integer port = nettyServerProperties.getPort();
        Integer maxReConnectTimes = nettyServerProperties.getMaxReConnectTimes();
        Long reConnectTnterval = nettyServerProperties.getReConnectTnterval();
        try {
            this.tryAutoConnect(name, host, port, reConnectTnterval, maxReConnectTimes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO 自动连接逻辑
     * @param serverName
     * @param serverHost
     * @param serverPort
     * @param interval
     * @param maxTimes
     * @throws InterruptedException
     */
    private void tryAutoConnect(String serverName, String serverHost, Integer serverPort, Long interval, Integer maxTimes) throws InterruptedException {
        int failTime = 1;
        NettyClient client = new NettyClient();
        NettyServerProperties serverProperties = new NettyServerProperties();
        serverProperties.setName(serverName);
        serverProperties.setHost(serverHost);
        serverProperties.setPort(serverPort);
        ConnectTask task = new ConnectTask(client, nettyProperties, serverProperties, false);
        Long second = 0l;
        while (!client.isConnect()) {
            if (failTime > maxTimes) {
                second = (failTime * interval) / 1000;
                logger.error("第" + failTime + "次尝试连接超时：" + second + "秒");
                logger.error("第" + failTime + "次尝试连接到 NettyServer (" + serverHost + ":" + serverPort + ")...");
                break;
            }
            logger.warn("第" + failTime + "次尝试连接到 NettyServer (" + serverHost + ":" + serverPort + ")...");
            startService.submit(task);
            Thread.sleep(interval);
            if (client.isConnect()) {
                break;
            }
            failTime++;
        }
        if (!client.isConnect()) {
            logger.info("连接失败(超时" + second + "秒) NettyServer (" + serverHost + ":" + serverPort + ")...");
            logger.info("稍后将在服务执行时，再次尝试连接...");
        }
    }



    // TODO 循环执行连接
    public static class ConnectTask implements Runnable {
        private NettyClient nettyClient;
        private NettyProperties nettyProperties;
        private NettyServerProperties nettyServerProperties;
        private boolean manual = false;

        public ConnectTask(NettyClient nettyClient, NettyProperties nettyProperties, NettyServerProperties nettyServerProperties, boolean manual) {
            this.nettyClient = nettyClient;
            this.nettyProperties = nettyProperties;
            this.nettyServerProperties = nettyServerProperties;
            this.manual = manual;
        }

        @Override
        public void run() {
            nettyClient.initNettyClient(nettyProperties, nettyServerProperties,manual);
        }

    }






}
