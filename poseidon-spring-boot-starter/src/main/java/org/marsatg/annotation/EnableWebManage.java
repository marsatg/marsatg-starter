package org.marsatg.annotation;



import org.marsatg.http.WebManageConstants;
import org.marsatg.http.WebManageController;
import org.marsatg.http.WebManageService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({WebManageConstants.class,WebManageService.class,WebManageController.class})/*,WebController.class*/
public @interface EnableWebManage {

    //TODO 服务端-记录客户端调用的次数
    boolean countClientCall() default false;

    //TODO 客户端-记录调用服务端的次数
    boolean countCallServer() default false;
}
