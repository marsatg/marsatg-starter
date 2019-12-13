package org.narsatg.poseidonprovider.service;

import org.marsatg.annotation.Method;
import org.marsatg.annotation.Service;
import org.springframework.stereotype.Component;

@Service(value = "hello",name = "hello服务",desc = "这是服务的描述")
@Component
public class ProviderService {


    @Method(value = "sayHello",desc = "这是一个sayHello方法")
    public String sayHello(String man){
        return "hello "+man;
    }


    @Method(value = "time",desc = "测试通道是否阻塞")
    public String time(String man) throws InterruptedException {
        if(man.equals("等待3秒")){
            Thread.sleep(3000);
        }
        return "hello "+man;
    }

}
