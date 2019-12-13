package org.marsatg.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NettyAccept implements Serializable {

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







    private Integer hash;

    private Object data;

    private boolean block = true;

    private String serviceName;

    private String methodName;

    private Object args[];

    @Override
    public String toString() {
        Map<String,Object> map = new HashMap<>();
        map.put("hash",hash);
        map.put("data",data);
        map.put("serviceName",serviceName);
        map.put("methodName",methodName);
        map.put("block",block);
        map.put("args",args);
        return JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
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

    public ByteBuf getByteBuf(){
        String json = this.toString();
        byte[] bytes = json.getBytes();
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        return byteBuf;
    }


}
