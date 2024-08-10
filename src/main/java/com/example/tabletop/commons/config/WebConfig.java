package com.example.tabletop.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		CorsRegistration corsRegistration = registry.addMapping("/**");

		// 기본 허용 출처 설정
		String[] defaultAllowedOrigins = {"http://3.37.149.248:80", "http://tabletop-backend"};

		// application.properties에서 설정을 읽어오되, 없으면 기본값 사용
		String customAllowedOrigins = System.getProperty("cors.allowed-origins");
		if (customAllowedOrigins != null && !customAllowedOrigins.isEmpty()) {
			corsRegistration.allowedOrigins(customAllowedOrigins.split(","));
		} else {
			corsRegistration.allowedOrigins(defaultAllowedOrigins);
		}

		corsRegistration
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}