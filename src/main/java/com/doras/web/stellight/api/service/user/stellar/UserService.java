package com.doras.web.stellight.api.service.user.stellar;

import com.doras.web.stellight.api.domain.user.Users;
import com.doras.web.stellight.api.domain.user.UsersRepository;
import com.doras.web.stellight.api.exception.UsersNotFoundException;
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
     * Check existence of user by snsId.
     * @param snsId SNS ID for user to be found
     * @return Always return {@code true}. It means successfully find the user object.
     *         If not found, {@link UsersNotFoundException} exception is thrown.
     */
    @Transactional(readOnly = true)
    public boolean existsBySnsId(String snsId) {
        usersRepository.findBySnsId(snsId)
                .orElseThrow(() -> new UsersNotFoundException(snsId));
        return true;
    }
}
