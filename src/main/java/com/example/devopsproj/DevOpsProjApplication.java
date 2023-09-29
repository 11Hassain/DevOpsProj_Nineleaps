package com.example.devopsproj;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "com.example.devopsproj")
public class DevOpsProjApplication {
	public static void main(String[] args) {
		SpringApplication.run(DevOpsProjApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				LoggerFactory.getLogger(getClass()).debug("Adding CORS mappings");
				registry.addMapping("/**")
						.allowedOrigins("https://example.com") // Specify trusted origins
						.allowedMethods("GET", "POST", "PUT", "DELETE") // Limit allowed methods
						.allowedHeaders("*"); // Allow all headers or specify needed headers
			}
		};
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}