package org.marsatg.boot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class Print implements BeanDefinitionRegistryPostProcessor{



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        String print = "\r\n  /\\/\\   __ _ _ __ ___  __ _| |_ __ _        / _ \\___  ___  ___(_) __| | ___  _ __  \n" +
                            " /    \\ / _` | '__/ __|/ _` | __/ _` |_____ / /_)/ _ \\/ __|/ _ \\ |/ _` |/ _ \\| '_ \\ \n" +
                            "/ /\\/\\ \\ (_| | |  \\__ \\ (_| | || (_| |_____/ ___/ (_) \\__ \\  __/ | (_| | (_) | | | |\n" +
                            "\\/    \\/\\__,_|_|  |___/\\__,_|\\__\\__, |     \\/    \\___/|___/\\___|_|\\__,_|\\___/|_| |_|\n" +
                            "                                |___/                                               \n";
        System.out.println(print);
    }
}
