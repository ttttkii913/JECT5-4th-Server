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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

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
                .cors(withDefaults())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 세션 정책을 stateless로 설정 -> 서버가 세션을 생성하지 않고 토큰 기반 인증을 사용하도록 설정 = jwt 방식
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/test/**").permitAll()
                        .requestMatchers( "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .requestMatchers("/api/v1/form-data/**").permitAll()
                        .requestMatchers("/api/v1/projects/users/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/profile/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/users/*/reviews").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/reviews/tags/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/reviews/spectrums/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/projects/*").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET,"/api/v1/users/*/projects/*/reviews").permitAll()
                        .anyRequest().authenticated()   // 그 외 모든 요청은 인증 필요
                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { // 패스워드 암호화
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // TODO: 쿠키 테스트를 위해 추가함, 추후 배포시 cors 관련 코드 삭제
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
