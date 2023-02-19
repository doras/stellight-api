package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import lombok.Getter;

@Getter
public class StellarsResponseDto {

    private final Long id;
    private final String nameKor;
    private final String nameEng;
    private final String nameJpn;

    public StellarsResponseDto(Stellar entity) {
        this.id = entity.getId();
        this.nameKor = entity.getNameKor();
        this.nameEng = entity.getNameEng();
        this.nameJpn = entity.getNameJpn();
    }
}
