package org.marsatg.boot;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class Print implements BeanFactoryPostProcessor{

    public static void main(String[] args) {
        //System.out.println(System.getProperties());
       new Print().postProcessBeanFactory(null);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("     __                          ");
        System.out.println("    ||\\\\    //                                                           ");
        System.out.println("    || \\\\  //                                                          ");
        System.out.println("    ||  \\\\//                                                           ");
        System.out.println("    ||   \\/                                                             ");


    }
}
