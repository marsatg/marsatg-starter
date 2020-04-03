package org.marsatg.annotation;



import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Service {

    String value() default "";

    String desc() default "";

    String name() default "";
}
