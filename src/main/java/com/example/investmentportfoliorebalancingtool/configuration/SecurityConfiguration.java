package com.example.investmentportfoliorebalancingtool.configuration;

import com.example.investmentportfoliorebalancingtool.controller.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URI;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository, CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(String.valueOf(URI.create("http://localhost:8080/")));
        return successHandler;
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {


        http.authorizeHttpRequests(request -> request
                        .requestMatchers( "/", "/about", "/learn", "/resources/**").permitAll()
                        .anyRequest().authenticated())
                        .oauth2Login((login) -> login.successHandler(customLoginSuccessHandler))
                        .logout((logout) -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler()));

        http.csrf((csrf) -> csrf.disable());
        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()));

        return http.build();
    }
}

