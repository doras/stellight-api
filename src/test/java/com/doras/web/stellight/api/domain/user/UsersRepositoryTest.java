package com.doras.web.stellight.api.domain.user;

import com.doras.web.stellight.api.exception.UsersNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link UsersRepository}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Clean up all repositories after each test.
     */
    @AfterEach
    public void cleanup() {
        usersRepository.deleteAll();
    }

    /**
     * Test saving user.
     */
    @Test
    public void saveUser() {
        //given
        LocalDateTime now = LocalDateTime.now();

        String snsId = "test-sns-oauth-id";
        Role role = Role.USER;
        Users user = usersRepository.save(Users.builder()
                .snsId(snsId)
                .role(role)
                .build());

        //when
        List<Users> usersList = usersRepository.findAll();

        //then
        Users userFound = usersList.get(0);
        assertThat(userFound.getId()).isEqualTo(user.getId());
        assertThat(userFound.getSnsId()).isEqualTo(snsId);
        assertThat(userFound.getRole()).isEqualTo(role);
        assertThat(userFound.getCreatedDateTime()).isAfterOrEqualTo(now);
    }

    /**
     * Test finding user by sns id.
     */
    @Test
    public void findBySnsId() {
        //given
        String snsId = "test-sns-oauth-id";
        Role role = Role.USER;
        Users user = usersRepository.save(Users.builder()
                .snsId(snsId)
                .role(role)
                .build());

        //when
        Users userFound = usersRepository.findBySnsId(snsId).orElseThrow(() -> new UsersNotFoundException(snsId));

        //then
        assertThat(userFound.getId()).isEqualTo(user.getId());
        assertThat(userFound.getSnsId()).isEqualTo(snsId);
        assertThat(userFound.getRole()).isEqualTo(role);
    }
}
