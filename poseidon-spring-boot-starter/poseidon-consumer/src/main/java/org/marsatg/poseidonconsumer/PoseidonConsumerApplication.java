package org.marsatg.poseidonconsumer;

import org.marsatg.annotation.EnableNettyClient;
import org.marsatg.annotation.EnableHttpClient;
import org.marsatg.annotation.EnableWebManage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNettyClient
@EnableWebManage(countCallServer = true,countClientCall = true)
public class PoseidonConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoseidonConsumerApplication.class, args);
	}

}
