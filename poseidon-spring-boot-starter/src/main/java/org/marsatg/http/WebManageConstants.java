package org.marsatg.http;

import org.marsatg.annotation.EnableWebManage;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

public class WebManageConstants implements ImportBeanDefinitionRegistrar{

    private static boolean countClientCall = false;

    private static boolean countCallServer = false;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        StandardAnnotationMetadata meta = (StandardAnnotationMetadata)metadata;
        Class<?> bootClass = meta.getIntrospectedClass();
        EnableWebManage enableWebManage = bootClass.getAnnotation(EnableWebManage.class);
        if(enableWebManage != null){
            countClientCall = enableWebManage.countClientCall();
            countCallServer = enableWebManage.countCallServer();
        }
    }

    public static boolean isCountClientCall(){
        return countClientCall;
    }

    public static boolean isCountCallServer() {
        return countCallServer;
    }
}
