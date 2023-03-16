package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO with {@link Schedule} information used in response.
 */
@Getter
public class ScheduleResponseDto {
    private final Long id;
    private final Long stellarId;
    private final String stellarNameKor;
    private final Boolean isFixedTime;
    private final LocalDateTime startDateTime;
    private final String title;
    private final String remark;

    public ScheduleResponseDto(Schedule entity) {
        this.id = entity.getId();
        this.stellarId = entity.getStellar().getId();
        this.stellarNameKor = entity.getStellar().getNameKor();
        this.isFixedTime = entity.getIsFixedTime();
        this.startDateTime = entity.getStartDateTime();
        this.title = entity.getTitle();
        this.remark = entity.getRemark();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleResponseDto that = (ScheduleResponseDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(stellarId, that.stellarId) &&
                Objects.equals(stellarNameKor, that.stellarNameKor) &&
                Objects.equals(isFixedTime, that.isFixedTime) &&
                Objects.equals(startDateTime, that.startDateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stellarId, stellarNameKor, isFixedTime, startDateTime, title, remark);
    }
}
