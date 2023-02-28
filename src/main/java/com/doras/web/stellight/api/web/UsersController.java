package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.config.auth.LoginUser;
import com.doras.web.stellight.api.config.auth.dto.SessionUser;
import com.doras.web.stellight.api.service.user.stellar.UserService;
import com.doras.web.stellight.api.web.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller about users.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {

    private final UserService userService;

    /**
     * Find user information of himself/herself
     * @param user session user DTO
     * @return Information of found user in {@link UserResponseDto}
     */
    @GetMapping("/me")
    public UserResponseDto findMe(@LoginUser SessionUser user) {
        return userService.findByEmail(user.getEmail());
    }
}
