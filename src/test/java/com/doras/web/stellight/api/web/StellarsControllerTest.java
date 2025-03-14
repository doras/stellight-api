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
        Byte generation = 2;
        Byte debutOrder = 1;
        String personalColor = "ffddaa";
        String emoji = "☪️";

        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .generation(generation)
                .debutOrder(debutOrder)
                .personalColor(personalColor)
                .emoji(emoji)
                .build());
        Long readId = savedStellar.getId();

        String url = "http://localhost:" + port + "/api/v1/stellars/" + readId;

        //when
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameKor", is(nameKor)))
                .andExpect(jsonPath("$.nameEng", is(nameEng)))
                .andExpect(jsonPath("$.nameJpn", is(nameJpn)))
                .andExpect(jsonPath("$.generation", is((int)generation)))
                .andExpect(jsonPath("$.debutOrder", is((int)debutOrder)))
                .andExpect(jsonPath("$.personalColor", is(personalColor)))
                .andExpect(jsonPath("$.emoji", is(emoji)));

    }

    /**
     * Test for getting all stellars
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void getAllStellars() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .emoji("☪️")
                .build());

        Stellar stellar2 = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름2")
                .nameEng("english name2")
                .nameJpn("日本語の名前2")
                .generation((byte)2)
                .debutOrder((byte)2)
                .personalColor("ffddaa")
                .emoji("⛩️")
                .build());

        String url = "http://localhost:" + port + "/api/v1/stellars";

        //when, then
        mvc.perform(get(url))
                // status check
                .andExpect(status().isOk())
                // list length check
                .andExpect(jsonPath("$.length()").value(2))
                // first element check
                .andExpect(jsonPath("$[0].id", is(stellar.getId()), Long.class))
                .andExpect(jsonPath("$[0].nameKor", is(stellar.getNameKor())))
                .andExpect(jsonPath("$[0].nameEng", is(stellar.getNameEng())))
                .andExpect(jsonPath("$[0].nameJpn", is(stellar.getNameJpn())))
                .andExpect(jsonPath("$[0].generation", is((int)stellar.getGeneration())))
                .andExpect(jsonPath("$[0].debutOrder", is((int)stellar.getDebutOrder())))
                .andExpect(jsonPath("$[0].personalColor", is(stellar.getPersonalColor())))
                .andExpect(jsonPath("$[0].emoji", is(stellar.getEmoji())))
                // second element check
                .andExpect(jsonPath("$[1].id", is(stellar2.getId()), Long.class))
                .andExpect(jsonPath("$[1].nameKor", is(stellar2.getNameKor())))
                .andExpect(jsonPath("$[1].nameEng", is(stellar2.getNameEng())))
                .andExpect(jsonPath("$[1].nameJpn", is(stellar2.getNameJpn())))
                .andExpect(jsonPath("$[1].generation", is((int)stellar2.getGeneration())))
                .andExpect(jsonPath("$[1].debutOrder", is((int)stellar2.getDebutOrder())))
                .andExpect(jsonPath("$[1].personalColor", is(stellar2.getPersonalColor())))
                .andExpect(jsonPath("$[1].emoji", is(stellar2.getEmoji())));
    }
}
