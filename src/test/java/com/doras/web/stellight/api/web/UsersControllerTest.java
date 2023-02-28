package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.config.auth.dto.SessionUser;
import com.doras.web.stellight.api.domain.user.Role;
import com.doras.web.stellight.api.domain.user.Users;
import com.doras.web.stellight.api.domain.user.UsersRepository;
import com.doras.web.stellight.api.web.dto.UserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                .email("testEmail@test.com")
                .role(Role.USER)
                .build());

        UserResponseDto expected = new UserResponseDto(user);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", new SessionUser(user));

        String url = "http://localhost:" + port + "/api/v1/users/me";

        //when, then
        mvc.perform(get(url).session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(expected.getEmail())));
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
}
