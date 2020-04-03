package org.marsatg.netty.factory;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * TODO 负载均衡组
 */
public class BalanceGroup {

    private Lock lock = new ReentrantLock();

    private String groupName;

    private int offset = 0;

    private ChannelHandlerContext[] channelArray;

    private int[] weights;

    private int size;

    public BalanceGroup(String groupName, int offset, ChannelHandlerContext[] channelArray, int[] weights) {
        Assert.state(channelArray != null && channelArray.length > 0,"BalanceGroup must have one channelArray that is not empty");
        Assert.state(weights != null && weights.length > 0,"BalanceGroup must have one weights that is not empty");
        Assert.state(channelArray.length == weights.length,"channelArray.length must equal weights.length");
        this.groupName = groupName;
        this.offset = offset;
        this.channelArray = channelArray;
        this.weights = weights;
        this.size = weights.length;
    }


    public ChannelHandlerContext ChannelHandlerContext() {
        ChannelHandlerContext context = null;
        try {
            lock.lock();
            context = channelArray[offset];
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return context;
    }

}
