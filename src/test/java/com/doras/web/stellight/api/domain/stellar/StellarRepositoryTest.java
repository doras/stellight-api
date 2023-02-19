package com.doras.web.stellight.api.domain.stellar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StellarRepositoryTest {

    @Autowired
    StellarRepository stellarRepository;

    @AfterEach
    public void cleanup() {
        stellarRepository.deleteAll();
    }

    @Test
    public void loadStellarsSave() {
        //given
        String nameKor = "테스트 한국어";
        String nameEng = "test english";
        String nameJpn = "テストの日本語";

        stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());

        //when
        List<Stellar> stellarList = stellarRepository.findAll();

        //then
        Stellar stellar = stellarList.get(0);
        assertThat(stellar.getNameKor()).isEqualTo(nameKor);
        assertThat(stellar.getNameEng()).isEqualTo(nameEng);
        assertThat(stellar.getNameJpn()).isEqualTo(nameJpn);
    }
}
