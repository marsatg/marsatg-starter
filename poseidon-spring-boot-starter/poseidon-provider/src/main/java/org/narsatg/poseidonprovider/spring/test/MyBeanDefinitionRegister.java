package org.narsatg.poseidonprovider.spring.test;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MyBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // 1 获得所有需要实例化代理的接口
        List<Class> classList = new ArrayList<>();
        classList.add(MyMapper.class);
        classList.add(MyMapper2.class);
        for(Class cs:classList){
            // 此处可通过循环所有的接口
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cs);
            // 设置为自定义的FactoryBean
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition)builder.getBeanDefinition();
            // 设置实例化的class
            beanDefinition.setBeanClass(MapperFactory.class);
            // 设置实例化的构造函数-->即：自定义的MapperFactory的实例化构造函数的参数
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(cs);
            String name = cs.getSimpleName();
            // 默认小驼峰命名
            name = name.substring(0,1).toLowerCase()+name.substring(1,name.length());
            registry.registerBeanDefinition(name,beanDefinition);
        }
        // 此处可通过循环所有的接口
        //BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyMapper.class);
        // 设置为自定义的FactoryBean
        //GenericBeanDefinition beanDefinition = (GenericBeanDefinition)builder.getBeanDefinition();
        // 设置实例化的class
        //beanDefinition.setBeanClass(MapperFactory.class);
        // 设置实例化的构造函数-->即：自定义的MapperFactory的实例化构造函数的参数
        //beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(MyMapper.class);
        //registry.registerBeanDefinition("myMapper",beanDefinition);


    }
}
