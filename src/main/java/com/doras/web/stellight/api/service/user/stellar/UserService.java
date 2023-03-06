package com.doras.web.stellight.api.service.user.stellar;

import com.doras.web.stellight.api.domain.user.Ban;
import com.doras.web.stellight.api.domain.user.BanRepository;
import com.doras.web.stellight.api.domain.user.Users;
import com.doras.web.stellight.api.domain.user.UsersRepository;
import com.doras.web.stellight.api.exception.UsersNotFoundException;
import com.doras.web.stellight.api.web.dto.BanSaveRequestDto;
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

    private final BanRepository banRepository;

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

    /**
     * Ban user with given id
     * @param id id of user who would be banned
     * @param requestDto DTO that has data to save
     * @return id of saved ban entity
     */
    @Transactional
    public Long banUser(Long id, BanSaveRequestDto requestDto) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new UsersNotFoundException(id));

        Ban ban = banRepository.save(Ban.builder()
                .users(user)
                .reason(requestDto.getReason())
                .build());

        return ban.getId();
    }
}
