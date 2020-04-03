package org.marsatg.annotation;


import org.omg.CORBA.Object;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(RequestMapping.class)
public @interface Method{

    String value() default "";

    boolean async() default false;

    String desc() default "";
}
