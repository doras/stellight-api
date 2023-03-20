package com.doras.web.stellight.api.domain.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link BanRepository}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BanRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BanRepository banRepository;

    /**
     * Clean up all repositories after each test.
     */
    @AfterEach
    public void cleanup() {
        banRepository.deleteAll();
        usersRepository.deleteAll();
    }

    /**
     * Test saving schedule.
     */
    @Test
    public void saveBan() {
        //given
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        Users user = usersRepository.save(Users.builder()
                .snsId("test-sns-oauth-id")
                .roles(Collections.singleton(Role.USER))
                .build());

        String reason = "test reason";
        Ban ban = banRepository.save(Ban.builder()
                .users(user)
                .reason(reason)
                .build());

        //when
        List<Ban> banList = banRepository.findAll();

        //then
        Ban banFound = banList.get(0);
        assertThat(banFound.getId()).isEqualTo(ban.getId());
        assertThat(banFound.getUsers().getId()).isEqualTo(user.getId());
        assertThat(banFound.getReason()).isEqualTo(reason);
        assertThat(banFound.getCreatedDateTime()).isAfterOrEqualTo(now);
    }
}
