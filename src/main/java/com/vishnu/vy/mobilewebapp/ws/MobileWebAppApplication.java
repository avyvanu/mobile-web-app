package com.vishnu.vy.mobilewebapp.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.vishnu.vy.mobilewebapp.ws.security.AppProperties;


@SpringBootApplication
public class MobileWebAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobileWebAppApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext getSpringApplicationContext() {
		return new SpringApplicationContext();
	}
	
	@Bean(name="AppProperties")
	public AppProperties getAppProperties()
	{
		return new AppProperties();
	}

}
