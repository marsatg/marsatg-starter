package org.marsatg.annotation;



import org.marsatg.http.WebManageConstants;
import org.marsatg.http.WebManageController;
import org.marsatg.http.WebManageService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({WebManageConstants.class,WebManageService.class,WebManageController.class})/*,WebController.class*/
public @interface EnableWebManage {

    //TODO 服务端-记录客户端调用的次数
    boolean countClientCall() default false;

    //TODO 客户端-记录调用服务端的次数
    boolean countCallServer() default false;
}
