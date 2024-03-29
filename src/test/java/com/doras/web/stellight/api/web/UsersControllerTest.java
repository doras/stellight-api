package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.config.auth.dto.SessionUser;
import com.doras.web.stellight.api.domain.user.*;
import com.doras.web.stellight.api.web.dto.BanSaveRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link UsersController}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    /**
     * Set up mvc before each test.
     */
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Clean up entityManager after each test.
     */
    @AfterEach
    public void cleanup() {
        banRepository.deleteAll();
        usersRepository.deleteAll();
    }

    /**
     * Test for getting current logged-in user
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    @WithMockUser(roles = "USER")
    public void findMe() throws Exception {
        //given
        Users user = usersRepository.save(Users.builder()
                .snsId("test-sns-id")
                .roles(Collections.singleton(Role.USER))
                .build());

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));

        String url = "http://localhost:" + port + "/api/v1/users/me";

        //when, then
        mvc.perform(get(url).session(mockHttpSession))
                .andExpect(status().isOk());
    }

    /**
     * Test for security configuration about findMe
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void findMeAuth() throws Exception {
        String url = "http://localhost:" + port + "/api/v1/users/me";
        mvc.perform(get(url))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    /**
     * Test banning user.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void banUser() throws Exception {

        //pre-load
        Users savedUser = usersRepository.save(Users.builder()
                .snsId("test-sns-id")
                .roles(Collections.singleton(Role.USER))
                .build());

        //given
        Long userId = savedUser.getId();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        String reason = "test reason";
        BanSaveRequestDto requestDto = BanSaveRequestDto.builder()
                .reason(reason)
                .build();

        String url = "http://localhost:" + port + "/api/v1/users/" + userId + "/ban";

        //when
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        Ban savedBan = banRepository.findAll().get(0);
        assertThat(savedBan.getUsers().getId()).isEqualTo(userId);
        assertThat(savedBan.getReason()).isEqualTo(reason);
        assertThat(savedBan.getCreatedDateTime()).isAfterOrEqualTo(now);
    }

    /**
     * Test role of banning user.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void failBanUserByRole() throws Exception {

        //pre-load
        Users savedUser = usersRepository.save(Users.builder()
                .snsId("test-sns-id")
                .roles(Collections.singleton(Role.USER))
                .build());

        //given
        BanSaveRequestDto requestDto = BanSaveRequestDto.builder()
                .reason("test reason")
                .build();

        String url = "http://localhost:" + port + "/api/v1/users/" + savedUser.getId() + "/ban";

        //when, then
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }
}
