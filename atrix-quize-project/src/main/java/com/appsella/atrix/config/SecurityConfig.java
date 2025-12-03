package com.appsella.atrix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Полностью игнорируем статические ресурсы
        web.ignoring()
                .antMatchers(
                        // старые пути статических ресурсов
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/favicon.ico",

                        // собранный React-фронтенд
                        "/static/**",
                        "/assets/**",
                        "/manifest.json",
                        "/asset-manifest.json",
                        "/logo192.png",
                        "/logo512.png",
                        "/robots.txt",
                        "/index.html",

                        // дополнительные статические страницы
                        "/new.html"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/index.html",
                        "/login.html",
                        "/error.html",
                        "/new.html",
                        "/Privacy.html",
                        "/TermsOfUse.html",
                        "/Billing.html",
                        "/Money.html",
                        "/api/**",
                        "/webhook/**",
                        "/health",
                        "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .headers()
                .frameOptions().sameOrigin(); // Для H2 Console
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}