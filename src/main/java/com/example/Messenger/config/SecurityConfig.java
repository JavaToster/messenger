package com.example.Messenger.config;

import com.example.Messenger.security.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthProvider authProvider;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers( "/error", "/bot-rest/**", "/rest-messenger/**", "/redis/**",
                                "/auth/*").permitAll()
                        .requestMatchers("/messenger/**", "/user/**").hasAnyRole("USER", "BLOCKER")
                        .requestMatchers("/admin/blocker/**").hasAnyRole("BLOCKER", "ADMIN")
                        .anyRequest().authenticated());
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(login -> login
                        .loginPage("/auth/login").loginProcessingUrl("/process_login")
                        .permitAll());

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(authProvider);
    }
}
