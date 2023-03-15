package com.doras.web.stellight.api.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO used in request to save {@link com.doras.web.stellight.api.domain.user.Ban}.
 */
@Getter
@NoArgsConstructor
public class BanSaveRequestDto {
    private String reason;

    @Builder
    public BanSaveRequestDto(String reason) {
        this.reason = reason;
    }
}
