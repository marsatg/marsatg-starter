package org.marsatg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.marsatg.annotation.ConfigField;
import org.marsatg.netty.NettyServerProperties;
import org.marsatg.properties.HttpProperties;
import org.marsatg.properties.HttpServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetaData {


    public static void main(String[] args) {
        //System.out.println("1");
        generate(HttpServerProperties.class);
    }


    public static void generate(Class<?> cls) {
        ConfigurationProperties configurationProperties = cls.getAnnotation(ConfigurationProperties.class);
        String prefix = configurationProperties.prefix();
        String sourceType = cls.getName();
        JSONObject groupJson = new JSONObject();
        groupJson.put("sourceType",sourceType);
        groupJson.put("name", prefix);
        groupJson.put("type",sourceType);
        Field[] declaredFields = cls.getDeclaredFields();
        JSONObject metaData = new JSONObject();
        List<JSONObject> properties = new ArrayList<>();
        for (Field f:declaredFields){
            ConfigField annotation = f.getAnnotation(ConfigField.class);
            if(annotation == null){
                continue;
            }
            JSONObject property = new JSONObject();
            property.put("sourceType",sourceType);
            property.put("name",prefix+"."+f.getName());
            property.put("type",f.getDeclaringClass());
            property.put("description",annotation.description());
            property.put("defaultValue",annotation.value());
            properties.add(property);
        }
        metaData.put("properties",properties);
        metaData.put("groups", Arrays.asList(groupJson));
        System.out.println(JSON.toJSONString(metaData, SerializerFeature.PrettyFormat));
    }
}
