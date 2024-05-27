package com.example.Messenger.config;

import com.example.Messenger.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserService userService;

    @Autowired
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(login -> login
                        .loginPage("/auth/login").loginProcessingUrl("/process_login")
                        .permitAll()
                        .defaultSuccessUrl("/messenger", true)
                        .failureUrl("/auth/login?error"));


        http
                .logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/user/auth")
                        .deleteCookies("username", "JSESSIONID").invalidateHttpSession(true));

        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/login", "/auth/register", "/error", "/bot-rest/**", "/rest-messenger/**", "/redis/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
