package com.romanm.jwtservicedata.configs.auth;

import com.romanm.jwtservicedata.components.auth.OpenUrlChecker;
import com.romanm.jwtservicedata.configs.auth.filters.JWTAuthorizationFilter;
import com.romanm.jwtservicedata.configs.auth.filters.OpenedPathsFilter;
import com.romanm.jwtservicedata.services.UserServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * Класс-конфигурация настроек безопасности с поддержкой проверки JWT
 */
@Configuration
@EnableWebFlux
@EnableReactiveMethodSecurity
@Profile(value = {"dev"})
public class SecurityConfiguration {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserServiceV1 userService;
    private final OpenUrlChecker openUrlChecker;

    /**
     * Конструктор класса SecurityConfiguration
     * @param bCryptPasswordEncoder BCryptPasswordEncoder
     * @param userService UserServiceV1
     * @param openUrlChecker OpenUrlChecker
     */
    @Autowired
    public SecurityConfiguration(BCryptPasswordEncoder bCryptPasswordEncoder, UserServiceV1 userService, OpenUrlChecker openUrlChecker) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.openUrlChecker = openUrlChecker;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager) {
       return http
               .csrf().disable()
               .httpBasic().disable()
               .formLogin().disable()
               .logout().disable()
               .addFilterAt(new OpenedPathsFilter(this.openUrlChecker), SecurityWebFiltersOrder.FIRST)
               .addFilterAt(new JWTAuthorizationFilter(this.userService), SecurityWebFiltersOrder.AUTHENTICATION)
               .build();
    }


}
