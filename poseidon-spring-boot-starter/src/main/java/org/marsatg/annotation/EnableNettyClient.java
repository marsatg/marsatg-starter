package org.marsatg.annotation;


import org.marsatg.netty.NettyClientProfileRegistrar;
import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.factory.ClientFactory;

import org.marsatg.netty.factory.ClientFactoryPostProcessor;
import org.marsatg.netty.factory.ServerFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({NettyClientProfileRegistrar.class,NettyProperties.class, ClientFactory.class, ClientFactoryPostProcessor.class})
public @interface EnableNettyClient {

    // TODO 当使用何种profile是，该配置生效
    String[] profiles() default "";

    boolean lazy() default false;
}
