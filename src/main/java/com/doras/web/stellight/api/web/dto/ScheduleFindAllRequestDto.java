package com.doras.web.stellight.api.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO with filters used when finding all schedules.
 */
@Getter
@NoArgsConstructor
public class ScheduleFindAllRequestDto {
    /**
     * ID of stellar.
     */
    private Long[] stellarId;

    /**
     * a filter that means
     * {@link com.doras.web.stellight.api.domain.schedule.Schedule}{@code #startDateTime} is before than this.
     */
    private LocalDateTime startDateTimeBefore;

    /**
     * a filter that means
     * {@link com.doras.web.stellight.api.domain.schedule.Schedule}{@code #startDateTime} is after than this.
     */
    private LocalDateTime startDateTimeAfter;

    @Builder
    public ScheduleFindAllRequestDto(Long[] stellarId, LocalDateTime startDateTimeBefore, LocalDateTime startDateTimeAfter) {
        this.stellarId = stellarId;
        this.startDateTimeBefore = startDateTimeBefore;
        this.startDateTimeAfter = startDateTimeAfter;
    }
}
