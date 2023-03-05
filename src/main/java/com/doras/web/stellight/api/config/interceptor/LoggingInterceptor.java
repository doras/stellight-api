package com.doras.web.stellight.api.config.interceptor;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Interceptor for logging configuration
 */
public class LoggingInterceptor implements HandlerInterceptor {

    /**
     * Interception point before the execution of a handler.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return {@code true}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // If user exists, add user id to MDC
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Optional.of(authentication.getPrincipal())
                    .map(p -> p instanceof DefaultOAuth2User ? (DefaultOAuth2User) p : null)
                    .map(u -> u.getAttribute("dbId"))
                    .ifPresent(o -> MDC.put("user", String.valueOf(o)));
        }
        return true;
    }

    /**
     * Interception point after successful execution of a handler.
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler that started asynchronous
     * execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     * (can also be {@code null})
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // clear MDC
        MDC.clear();
    }
}
