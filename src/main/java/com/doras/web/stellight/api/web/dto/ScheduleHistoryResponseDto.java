package com.doras.web.stellight.api.web.dto;

import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO with {@link ScheduleHistory} information used in response.
 */
@Getter
public class ScheduleHistoryResponseDto {
    private final Long id;
    private final Long scheduleId;
    private final Boolean isFixedTime;
    private final LocalDateTime startDateTime;
    private final String title;
    private final String remark;
    private final String createdBy;

    public ScheduleHistoryResponseDto(ScheduleHistory entity) {
        this.id = entity.getId();
        this.scheduleId = entity.getSchedule().getId();
        this.isFixedTime = entity.getIsFixedTime();
        this.startDateTime = entity.getStartDateTime();
        this.title = entity.getTitle();
        this.remark = entity.getRemark();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleHistoryResponseDto that = (ScheduleHistoryResponseDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(scheduleId, that.scheduleId) &&
                Objects.equals(isFixedTime, that.isFixedTime) &&
                Objects.equals(startDateTime, that.startDateTime) &&
                Objects.equals(title, that.title) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scheduleId, isFixedTime, startDateTime, title, remark, createdBy);
    }
}
