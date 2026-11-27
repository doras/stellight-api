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
public class ScheduleSaveRequestDto {
    private Long stellarId;
    private Boolean isFixedTime;
    private Boolean isDayOff;
    private LocalDateTime startDateTime;
    private LocalDateTime startDateTimeUntil;
    private String title;
    private String remark;

    @Builder
    public ScheduleSaveRequestDto(Long stellarId, Boolean isFixedTime, Boolean isDayOff,
                                  LocalDateTime startDateTime,
                                  LocalDateTime startDateTimeUntil, String title, String remark) {
        this.stellarId = stellarId;
        this.isFixedTime = isFixedTime;
        this.isDayOff = isDayOff;
        this.startDateTime = startDateTime;
        this.startDateTimeUntil = startDateTimeUntil;
        this.title = title;
        this.remark = remark;
    }

    /**
     * Create new {@link Schedule} object with values in this object.
     * @return created {@link Schedule} object.
     */
    public Schedule toScheduleEntity() {
        return Schedule.builder()
                .isFixedTime(isFixedTime)
                .isDayOff(isDayOff)
                .startDateTime(startDateTime)
                .startDateTimeUntil(startDateTimeUntil)
                .title(title)
                .remark(remark)
                .build();
    }
}
