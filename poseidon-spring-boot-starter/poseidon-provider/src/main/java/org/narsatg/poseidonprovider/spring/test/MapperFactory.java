package org.narsatg.poseidonprovider.spring.test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;


@Component
public class MapperFactory implements FactoryBean {

    Class mapperInterface;

    public MapperFactory(Class mapperInterface) {
        System.out.println("MapperFactory 构造器："+mapperInterface);
        this.mapperInterface = mapperInterface;
    }

    public MapperFactory() {
    }

    @Override
    public Object getObject() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Class[] cls = new Class[]{mapperInterface};
        MyInvocationHandler handler = new MyInvocationHandler();
        Object o = Proxy.newProxyInstance(classLoader, cls, handler);
        return o;
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
