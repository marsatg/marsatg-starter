package org.marsatg.annotation;



import org.marsatg.http.factory.ClientTempleteFactory;
import org.marsatg.poseidon.PoseidonRequestService;
import org.marsatg.properties.HttpProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({HttpProperties.class,ClientTempleteFactory.class, PoseidonRequestService.class})
public @interface EnableHttpClient {
}
