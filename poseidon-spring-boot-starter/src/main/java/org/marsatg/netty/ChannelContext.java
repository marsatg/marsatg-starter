package org.marsatg.netty;


import io.netty.channel.ChannelHandlerContext;

import org.marsatg.http.WebManageConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * TODO 客户端上下文容器类
 */
public class ChannelContext {

    public static Logger logger = LoggerFactory.getLogger(ChannelContext.class);
    private static Map<String, ChannelHandlerContext> contextMap = new ConcurrentHashMap<>();
    private static Map<String, Long> callServerCountMap = new ConcurrentHashMap<>();

    public static Map<String, ChannelHandlerContext> getContextMap() {
        return contextMap;
    }

    ;

    public static Map<String, Long> getcallServerCountMap() {
        return callServerCountMap;
    }

    ;

    public static void initChannelContext(String serverName, ChannelHandlerContext channelHandlerContext) {
        contextMap.put(serverName, channelHandlerContext);
        callServerCountMap.put(serverName, 0L);
    }

    public static void saveCallServerCount(String serverName) {
        if (WebManageConstants.isCountCallServer()) {
            Long call = callServerCountMap.get(serverName);
            call = call == null ? 0 : call;
            call += 1;
            callServerCountMap.put(serverName, call);
        }
    }


}
