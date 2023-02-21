package com.doras.web.stellight.api.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleFindAllRequestDto {
    private Long stellarId;
    private LocalDateTime startDateTimeBefore;
    private LocalDateTime startDateTimeAfter;
}
