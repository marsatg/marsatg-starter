package org.marsatg.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NettyAccept implements Serializable {

    private static final SerializerFeature FEATURE = SerializerFeature.WriteMapNullValue;


    private int state = 200;

    private boolean useUrl = false;

    private String url;

    private Integer hash;

    private Object data;

    private boolean block = true;

    private String serviceName;

    private String methodName;

    private Object args[];

    @Override
    public String toString() {
        Map<String,Object> map = new HashMap<>();
        map.put("state",state);
        map.put("useUrl",useUrl);
        map.put("url",url);
        map.put("hash",hash);
        map.put("data",data);
        map.put("serviceName",serviceName);
        map.put("methodName",methodName);
        map.put("block",block);
        map.put("args",args);
        return JSON.toJSONString(map,FEATURE);
    }







    public NettyAccept() {
    }

    public NettyAccept accept(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        byte[] raw = new byte[length];
        byteBuf.readBytes(raw);
        String json = new String(raw);
        try {
            return JSON.parseObject(json, NettyAccept.class);
        }
        catch (Exception e){
            System.out.println("JSON:"+json);
            e.printStackTrace();
            return null;
        }


    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isUseUrl() {
        return useUrl;
    }

    public void setUseUrl(boolean useUrl) {
        this.useUrl = useUrl;
    }

    public ByteBuf getByteBuf(){
        String json = this.toString();
        byte[] bytes = json.getBytes();
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        return byteBuf;
    }


}
