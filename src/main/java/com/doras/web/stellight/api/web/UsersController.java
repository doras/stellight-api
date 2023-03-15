package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.config.auth.LoginUser;
import com.doras.web.stellight.api.config.auth.dto.SessionUser;
import com.doras.web.stellight.api.service.user.stellar.UserService;
import com.doras.web.stellight.api.web.dto.BanSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller about users.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    /**
     * Find user information of himself/herself
     * @param user session user DTO
     * @return {@code true} when the user is found. Otherwise, throw exception.
     */
    @GetMapping("/me")
    public boolean findMe(@LoginUser SessionUser user) {
        logger.info("find me");
        return userService.existsBySnsId(user.getSnsId());
    }

    /**
     * Ban user with given id
     * @param id id of user who would be banned
     * @param requestDto DTO that has data to save
     * @return id of saved ban entity
     */
    @PostMapping("/{id}/ban")
    public Long banUser(@PathVariable Long id, @RequestBody BanSaveRequestDto requestDto) {
        logger.info("ban user id = {}", id);
        return userService.banUser(id, requestDto);
    }
}
