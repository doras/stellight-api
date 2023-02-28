package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.user.Users;
import lombok.Getter;

/**
 * DTO with {@link Users} information used in response.
 */
@Getter
public class UserResponseDto {

    /**
     * Email address of user
     */
    private final String email;

    public UserResponseDto(Users user) {
        this.email = user.getEmail();
    }
}
