package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link StellarsController}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StellarsControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StellarRepository stellarRepository;

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
                .build();
    }

    /**
     * Clean up entityManager after each test.
     */
    @AfterEach
    public void cleanup() {
        stellarRepository.deleteAll();
    }

    /**
     * Test for getting stellar
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void getStellar() throws Exception {
        //given
        String nameKor = "한국 이름";
        String nameEng = "english name";
        String nameJpn = "日本語の名前";

        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());
        Long readId = savedStellar.getId();

        String url = "http://localhost:" + port + "/api/v1/stellars/" + readId;

        //when
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameKor", is(nameKor)))
                .andExpect(jsonPath("$.nameEng", is(nameEng)))
                .andExpect(jsonPath("$.nameJpn", is(nameJpn)));

    }
}
