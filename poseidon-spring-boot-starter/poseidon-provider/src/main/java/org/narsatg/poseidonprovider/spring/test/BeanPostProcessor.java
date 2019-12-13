package org.narsatg.poseidonprovider.spring.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;


@Component
public class BeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ScannedGenericBeanDefinition myService = (ScannedGenericBeanDefinition)beanDefinitionRegistry.getBeanDefinition("myService");
        myService.setBeanClassName("org.narsatg.poseidonprovider.spring.test.MyService2");
        //myService.setBeanClass(MyService2.class);

        //System.out.println(myService);
    }
}
