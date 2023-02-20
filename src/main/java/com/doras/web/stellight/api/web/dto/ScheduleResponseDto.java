package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponseDto {
    private final Long id;
    private final String stellarNameKor;
    private final Boolean isFixedTime;
    private final LocalDateTime startDateTime;
    private final String title;
    private final String remark;

    public ScheduleResponseDto(Schedule entity) {
        this.id = entity.getId();
        this.stellarNameKor = entity.getStellar().getNameKor();
        this.isFixedTime = entity.getIsFixedTime();
        this.startDateTime = entity.getStartDateTime();
        this.title = entity.getTitle();
        this.remark = entity.getRemark();
    }
}
