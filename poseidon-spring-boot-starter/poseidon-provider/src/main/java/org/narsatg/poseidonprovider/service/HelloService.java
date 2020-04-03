package org.narsatg.poseidonprovider.service;

import com.alibaba.fastjson.JSONObject;
import org.marsatg.annotation.Method;
import org.marsatg.annotation.Service;
import org.narsatg.poseidonprovider.Person;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Service(value = "hello",name = "hello服务",desc = "这是服务的描述")
@Component
public class HelloService {


    @Transactional(rollbackFor = Exception.class)
    @Method(value = "sayHello",desc = "这是一个sayHello方法")
    public String sayHello(String man){
        return "hello "+man;
    }


    @Method(value = "time",desc = "测试通道是否阻塞")
    public String time(@RequestBody Map man) throws InterruptedException {
        if(man.equals("等待3秒")){
            Thread.sleep(3000);
        }
        return "hello "+man;
    }


    @Method(value = "object",desc = "对象类型测试")
    public Person Person(@RequestBody List object, String cll) throws InterruptedException {
        Person person = new Person();
        return person;
    }
}
