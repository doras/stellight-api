package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import lombok.Getter;

/**
 * DTO with {@link Stellar} information used in response.
 */
@Getter
public class StellarResponseDto {

    private final Long id;
    private final String nameKor;
    private final String nameEng;
    private final String nameJpn;
    private final Byte generation;
    private final Byte debutOrder;
    private final String personalColor;
    private final String emoji;

    public StellarResponseDto(Stellar entity) {
        this.id = entity.getId();
        this.nameKor = entity.getNameKor();
        this.nameEng = entity.getNameEng();
        this.nameJpn = entity.getNameJpn();
        this.generation = entity.getGeneration();
        this.debutOrder = entity.getDebutOrder();
        this.personalColor = entity.getPersonalColor();
        this.emoji = entity.getEmoji();
    }
}
