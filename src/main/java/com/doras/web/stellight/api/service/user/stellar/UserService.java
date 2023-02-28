package com.doras.web.stellight.api.service.user.stellar;

import com.doras.web.stellight.api.domain.user.Users;
import com.doras.web.stellight.api.domain.user.UsersRepository;
import com.doras.web.stellight.api.exception.UsersNotFoundException;
import com.doras.web.stellight.api.web.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service about {@link Users}.
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UsersRepository usersRepository;

    /**
     * Find user by email.
     * @param email email address for user to be found
     * @return information of found entity in {@link UserResponseDto}
     */
    @Transactional(readOnly = true)
    public UserResponseDto findByEmail(String email) {
        Users entity = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersNotFoundException(email));
        return new UserResponseDto(entity);
    }
}
