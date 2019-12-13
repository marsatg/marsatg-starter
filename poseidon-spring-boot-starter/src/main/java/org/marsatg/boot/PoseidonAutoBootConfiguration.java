package org.marsatg.boot;


import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;

//定义为配置类
@Configuration
//在web工程条件下成立
@ConditionalOnWebApplication
public class PoseidonAutoBootConfiguration {
}
