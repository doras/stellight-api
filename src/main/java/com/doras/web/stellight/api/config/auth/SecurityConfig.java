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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
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

    private final CustomSessionInformationExpiredStrategy customSessionInformationExpiredStrategy;

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
                .mvcMatchers("/", "/favicon.ico", "/assets/**", "/statics/**").permitAll() // allow for static resources
                .antMatchers("/h2-console/**").permitAll() // allow for h2 console
                .mvcMatchers("/api/v1/users/me").authenticated() // only authenticated user for /user/me API
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
                .failureUrl("/statics/login-fail.html")
            )
            .sessionManagement(session -> session
                    .maximumSessions(1)
                    .sessionRegistry(sessionRegistry())
                    .expiredSessionStrategy(customSessionInformationExpiredStrategy)
            );

        return http.build();
    }

    /**
     * Configuration class for sending 401 error not redirect to login url.
     */
    private static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            int code = HttpServletResponse.SC_UNAUTHORIZED;
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(code);
            String msg = "로그인 후 이용 가능합니다.";
            response.getWriter().write("{\"code\":"+code+",\"message\": \"" + msg + "\"}");
        }
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
