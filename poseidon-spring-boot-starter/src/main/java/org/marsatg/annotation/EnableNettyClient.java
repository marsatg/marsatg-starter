package org.marsatg.annotation;


import org.marsatg.netty.NettyProperties;
import org.marsatg.netty.factory.ClientFactory;
import org.marsatg.netty.factory.ServerFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({NettyProperties.class, ClientFactory.class})
public @interface EnableNettyClient {
}
