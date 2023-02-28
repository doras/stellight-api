package com.doras.web.stellight.api.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

/**
 * Exception for banned user thrown when banned user request login.
 */
public class BannedUserException extends OAuth2AuthenticationException {

    /**
     * Default constructor with default message
     */
    public BannedUserException() {
        super(new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED), "접근이 금지된 사용자입니다.");
    }
}
