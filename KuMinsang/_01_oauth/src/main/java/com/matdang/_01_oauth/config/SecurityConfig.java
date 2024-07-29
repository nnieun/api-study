package com.matdang._01_oauth.config;

import com.matdang._01_oauth.auth.dto.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf)->csrf.disable())
                .formLogin((login)->login.disable())
                .httpBasic((httpBasic)->httpBasic.disable())
                .oauth2Login((oauth2)->oauth2.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService)))
                .authorizeHttpRequests(
                        (auth)->auth
                                .requestMatchers("/")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );
        return http.build();
    }
}
