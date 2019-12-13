package org.marsatg.annotation;



import org.marsatg.proxy.BeanProxyFactory;
import org.marsatg.http.ServerController;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({BeanProxyFactory.class,ServerController.class})
public @interface EnableHttpServer {

}
