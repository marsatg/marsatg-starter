package org.narsatg.poseidonprovider;


import org.marsatg.annotation.EnableHttpServer;
import org.marsatg.annotation.EnableNettyServer;
import org.marsatg.annotation.EnableWebManage;

import org.narsatg.poseidonprovider.spring.test.OneService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableNettyServer
@EnableWebManage(countClientCall = true,countCallServer = true)
@Import(OneService.class)
public class PoseidonProviderApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PoseidonProviderApplication.class, args);

	}


	public void test(){

	}

}
