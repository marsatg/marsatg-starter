package org.marsatg.boot;


import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

//定义为配置类
@Configuration
//在web工程条件下成立
@ConditionalOnWebApplication
@Import(Print.class)
public class PoseidonAutoBootConfiguration {
}
