package com.doras.web.stellight.api.config.auth.dto;

import com.doras.web.stellight.api.domain.user.Users;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for user information to save in session attribute
 */
@Getter
public class SessionUser implements Serializable {

    /**
     * SNS ID of user
     */
    private final String snsId;

    public SessionUser(Users user) {
        this.snsId = user.getSnsId();
    }
}
