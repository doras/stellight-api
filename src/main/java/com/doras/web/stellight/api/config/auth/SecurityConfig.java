package com.doras.web.stellight.api.config.auth;

import com.doras.web.stellight.api.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security Configuration class
 */
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOauth2UserService customOauth2UserService;

    /**
     * HttpSecurity filter chain configuration bean
     */
    @Bean
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            )
            .authorizeHttpRequests(authorize -> authorize
                .mvcMatchers("/", "/favicon.ico", "/assets/**").permitAll() // allow for static resources
                .antMatchers("/h2-console/**").permitAll() // allow for h2 console
                .mvcMatchers(HttpMethod.GET, "/api/v1/**").permitAll() // permit all for GET API
                .mvcMatchers(HttpMethod.POST, "/api/v1/**").hasRole(Role.USER.name()) // only USER can access to POST API
                .mvcMatchers(HttpMethod.PUT, "/api/v1/**").hasRole(Role.USER.name()) // only USER can access to PUT API
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole(Role.USER.name()) // only USER can access to DELETE API
                .anyRequest().denyAll()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                    .userService(customOauth2UserService)
                )
            );

        return http.build();
    }

    /**
     * Configuration class for sending 401 error not redirect to login url.
     */
    private static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}
