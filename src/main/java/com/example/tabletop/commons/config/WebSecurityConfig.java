package com.example.tabletop.commons.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.tabletop.commons.filter.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests((request) -> request
                		.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // test
                		.requestMatchers("/api/sales/*").permitAll()
                		.requestMatchers("/api/sse/notify/*").permitAll()
                        .requestMatchers("/api/sellers/signup", "/api/sellers/exists").permitAll()
                        .requestMatchers("/api/stores/*/menus", "/api/stores/*", "/api/stores/*/details").permitAll()
                        .requestMatchers("/api/orders/*").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/{loginId}", "/api/auth/token/refresh").permitAll()
                        .requestMatchers("/api/mail/**", "/api/s3/**", "/api/store/**").permitAll()
                        // 임시방편으로
                        .requestMatchers("/api/**").permitAll() // 이렇게는 어떠심
                        
                        .anyRequest().authenticated()
                )
                
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    /*
		AuthenticationManager
	      - 인증 처리를 해준다
          - 실제론 AuthenticationManager 인터페이스가 ProviderManager 에게 인증을 위임하는 것
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}