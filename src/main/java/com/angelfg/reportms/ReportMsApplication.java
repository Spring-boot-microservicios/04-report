package com.angelfg.reportms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ReportMsApplication /*implements CommandLineRunner*/ {

//	@Autowired
//	private EurekaClient eurekaClient;

	public static void main(String[] args) {
		SpringApplication.run(ReportMsApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		this.eurekaClient.getAllKnownRegions().forEach(System.out::println);
//		System.out.println(this.eurekaClient.getApplication("companies-crud"));
//	}

}
