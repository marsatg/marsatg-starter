package org.marsatg.poseidonconsumer;

import org.marsatg.annotation.EnableNettyClient;
import org.marsatg.annotation.EnableHttpClient;
import org.marsatg.annotation.EnableWebManage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableNettyClient()
@EnableWebManage(countCallServer = true,countClientCall = true)
public class PoseidonConsumerApplication {
	static ExecutorService service = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(PoseidonConsumerApplication.class, args);
		/*Object bean = run.getBean("testClient1");
		System.out.println(bean);*/
	}

}
