package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO used in request to save {@link Schedule}.
 */
@Getter
@NoArgsConstructor
public class ScheduleUpdateRequestDto {
    private Boolean isFixedTime;
    private Boolean isDayOff;
    private LocalDateTime startDateTime;
    private LocalDateTime startDateTimeUntil;
    private String title;
    private String remark;

    @Builder
    public ScheduleUpdateRequestDto(Boolean isFixedTime, Boolean isDayOff,
                                    LocalDateTime startDateTime,
                                    LocalDateTime startDateTimeUntil, String title, String remark) {
        this.isFixedTime = isFixedTime;
        this.isDayOff = isDayOff;
        this.startDateTime = startDateTime;
        this.startDateTimeUntil = startDateTimeUntil;
        this.title = title;
        this.remark = remark;
    }
}
