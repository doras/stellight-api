package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleSaveRequestDto {
    private Long stellarId;
    private Boolean isFixedTime;
    private LocalDateTime startDateTime;
    private String title;
    private String remark;

    @Builder
    public ScheduleSaveRequestDto(Long stellarId, Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
        this.stellarId = stellarId;
        this.isFixedTime = isFixedTime;
        this.startDateTime = startDateTime;
        this.title = title;
        this.remark = remark;
    }

    public Schedule toScheduleEntity() {
        return Schedule.builder()
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build();
    }
}
