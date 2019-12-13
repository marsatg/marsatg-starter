package org.narsatg.poseidonprovider;


import org.marsatg.annotation.EnableHttpServer;
import org.marsatg.annotation.EnableNettyServer;
import org.marsatg.annotation.EnableWebManage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableNettyServer
@EnableWebManage(countClientCall = true,countCallServer = true)
public class PoseidonProviderApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PoseidonProviderApplication.class, args);
		//MyMapper myMapper = (MyMapper)context.getBean("myMapper");
		//System.out.println(myMapper);
		//MyMapper2 myMapper2 = (MyMapper2)context.getBean("myMapper2");
		//System.out.println(myMapper);
		//System.out.println(myMapper2);
		//myMapper.print();
		//Object serverFactory = context.getBean(ServerFactory.class);
		//System.out.println();
	}

}
