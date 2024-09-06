package com.mbb.api.sales_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalesTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesTrackerApplication.class, args);
	}

}
