package com.sossbar.global.config;

import com.sossbar.oauth2.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 세션 정책을 stateless로 설정 -> 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정 = jwt 방식
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .anyRequest().authenticated()   // 그 외 모든 요청은 인증 필요
                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { // 패스워드 암호화
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
