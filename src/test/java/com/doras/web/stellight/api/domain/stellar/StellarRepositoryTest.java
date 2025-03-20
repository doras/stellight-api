package com.doras.web.stellight.api.domain.stellar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link StellarRepository}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StellarRepositoryTest {

    @Autowired
    private StellarRepository stellarRepository;

    /**
     * Clean up all repositories after each test.
     */
    @AfterEach
    public void cleanup() {
        stellarRepository.deleteAll();
    }

    /**
     * Test for loading saved stellars.
     */
    @Test
    public void loadStellarsSave() {
        //given
        String nameKor = "테스트 한국어";
        String nameEng = "test english";
        String nameJpn = "テストの日本語";
        Byte generation = 2;
        Byte debutOrder = 1;
        String personalColor = "00ff66";
        String emoji = "☪️";
        Boolean isGraduated = false;

        stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .generation(generation)
                .debutOrder(debutOrder)
                .personalColor(personalColor)
                .emoji(emoji)
                .isGraduated(isGraduated)
                .build());

        //when
        List<Stellar> stellarList = stellarRepository.findAll();

        //then
        Stellar stellar = stellarList.get(0);
        assertThat(stellar.getNameKor()).isEqualTo(nameKor);
        assertThat(stellar.getNameEng()).isEqualTo(nameEng);
        assertThat(stellar.getNameJpn()).isEqualTo(nameJpn);
        assertThat(stellar.getGeneration()).isEqualTo(generation);
        assertThat(stellar.getDebutOrder()).isEqualTo(debutOrder);
        assertThat(stellar.getPersonalColor()).isEqualTo(personalColor);
        assertThat(stellar.getEmoji()).isEqualTo(emoji);
        assertThat(stellar.getIsGraduated()).isEqualTo(isGraduated);
    }
}
