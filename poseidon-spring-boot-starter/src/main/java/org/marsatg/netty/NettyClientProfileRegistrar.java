package org.marsatg.netty;

import org.apache.commons.lang3.StringUtils;
import org.marsatg.annotation.EnableNettyClient;
import org.marsatg.annotation.EnableWebManage;
import org.marsatg.netty.factory.ClientFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

public class NettyClientProfileRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static String usingProfiles = "";

    @Override
    public void setEnvironment(Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length != 0) {
            usingProfiles = activeProfiles[0];
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        StandardAnnotationMetadata meta = (StandardAnnotationMetadata) metadata;
        Class<?> bootClass = meta.getIntrospectedClass();
        EnableNettyClient enableNettyClient = bootClass.getAnnotation(EnableNettyClient.class);
        if (enableNettyClient != null) {
            boolean active = true;
            boolean lazy = enableNettyClient.lazy();
            String[] profiles = enableNettyClient.profiles();
            boolean empty = (profiles == null || profiles.length == 0) || (profiles.length == 1 && StringUtils.isBlank(profiles[0]));
            if (!empty) {
                // TODO 如果指定的profiles包含了当前启动的环境 -> 保持客户端启动
                active = isActive(profiles);
            }
            ClientFactory.setActive(active);
            ClientFactory.setLazy(lazy);
        }
    }


    private static boolean isActive(String[] nettyProfiles) {
        boolean isBootProfile = StringUtils.isNotBlank(usingProfiles);
        if (isBootProfile) {
            // TODO 同时指定了netty配置，启动配置
            for (String profile : nettyProfiles) {
                if (profile.equals(usingProfiles)) {
                    return true;
                }
            }
        }
        return false;
    }

}
