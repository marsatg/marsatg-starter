package org.marsatg.netty.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ClientFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //String[] names = registry.getBeanDefinitionNames();
       /* String client = "org.marsatg.netty.factory.ClientFactory";
        BeanDefinition beanDefinition = registry.getBeanDefinition(client);
        beanDefinition.setDependsOn("org.marsatg.netty.NettyProperties");
        registry.registerBeanDefinition(client,beanDefinition);*/
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
