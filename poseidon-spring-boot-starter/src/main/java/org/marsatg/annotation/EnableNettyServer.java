package org.marsatg.annotation;


import org.marsatg.netty.factory.ServerFactoryPostProcessor;
import org.marsatg.proxy.BeanProxyFactory;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.factory.ServerFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({BeanProxyFactory.class,NettyProperties.class,ServerFactory.class, ServerFactoryPostProcessor.class})
public @interface EnableNettyServer {
}
