package com.example.RestTemplate;


import com.example.RestTemplate.config.LoggingInterceptor;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "REST TEMPLATE", description = "Rest template service", version = "2.0"))
public class RestTemplateApplication {

	public static void main(String[] args) {

		SpringApplication.run(RestTemplateApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		// Add the LoggingInterceptor to the RestTemplate
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new LoggingInterceptor());
		restTemplate.setInterceptors(interceptors);
//
//		// Use the RestTemplate as usual
//		String url = "http://localhost:8080/vending-machine/total-items";
//		String responseBody = restTemplate.getForObject(url,String.class);
//		System.out.println("Response from server: " + responseBody);

		return  restTemplate;
	}




}















