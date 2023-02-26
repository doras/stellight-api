package com.doras.web.stellight.api.web.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO with filters used when finding all schedules.
 */
@Getter
@RequiredArgsConstructor
public class ScheduleFindAllRequestDto {

    /**
     * ID of stellar.
     */
    private final Long[] stellarId;

    /**
     * a filter that means
     * {@link com.doras.web.stellight.api.domain.schedule.Schedule}{@code #startDateTime} is before than this.
     */
    private final LocalDateTime startDateTimeBefore;

    /**
     * a filter that means
     * {@link com.doras.web.stellight.api.domain.schedule.Schedule}{@code #startDateTime} is after than this.
     */
    private final LocalDateTime startDateTimeAfter;
}
