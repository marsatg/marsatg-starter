package org.marsatg.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.NettyServerProperties;


public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private NettyProperties nettyProperties;

    private NettyServerProperties nettyServerProperties;



    public ClientInitializer(NettyProperties nettyProperties,NettyServerProperties nettyServerProperties) {
        this.nettyProperties = nettyProperties;
        this.nettyServerProperties = nettyServerProperties;
    }

    public ClientInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        ClientHandler clientHandler = new ClientHandler(nettyProperties,nettyServerProperties);
        pipeline.addLast(clientHandler);
    }
}
