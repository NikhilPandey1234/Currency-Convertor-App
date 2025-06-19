package com.currency.CurrencyConvertor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyConvertorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConvertorApplication.class, args);
	}

}
