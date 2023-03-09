package com.doras.web.stellight.api.config.auth;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * custom session expired strategy class
 */
@Component
public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        var request = event.getRequest();
        var response = event.getResponse();

        String uri = request.getRequestURI();

        // when method is not GET, 401 error with message.
        if (!request.getMethod().equals("GET")) {
            int code = HttpServletResponse.SC_UNAUTHORIZED;
            String msg = "세션이 만료되었습니다.";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(code);
            response.getWriter().write("{\"code\":"+code+",\"message\": \"" + msg + "\"}");
        // when GET method, redirect to same url.
        } else {
            String queryString = request.getQueryString();
            if (queryString != null) {
                uri += "?" + queryString;
            }
            response.sendRedirect(uri);
        }
    }
}
