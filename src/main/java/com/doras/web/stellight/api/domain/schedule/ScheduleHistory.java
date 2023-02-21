package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.CreatedTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * History entity for {@link Schedule}.
 * Track creation or modification history for {@link Schedule}.
 */
@Getter
@NoArgsConstructor
@Entity
public class ScheduleHistory extends CreatedTimeEntity {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * (Required) Relationship to {@link Schedule}, real data of the history.
     */
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    /**
     * (Required) Whether the time of schedule is fixed or not.
     * If not, use only Date information of {@link #startDateTime} field.
     */
    @Column(nullable = false)
    private Boolean isFixedTime;

    /**
     * (Required) Date and time when the schedule starts.
     * If {@link #isFixedTime} is false, time information is not used.
     */
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    /**
     * (Required) Title of schedule.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Remark for schedule. Not necessary.
     */
    @Column(length = 500)
    private String remark;

    /**
     * Constructor of ScheduleHistory.
     * @param schedule Value for {@link #schedule}. The original data of this history.
     * @param isFixedTime
     *        Value for {@link #isFixedTime}.
     *        If false, truncate {@code startDateTime} to days. That is, using only date information.
     * @param startDateTime
     *        Value for {@link #startDateTime}.
     *        If {@code isFixedTime} is false, truncate to days. That is, using only date information.
     * @param title Value for {@link #title}.
     * @param remark Value for {@link #remark}.
     */
    @Builder
    public ScheduleHistory(Schedule schedule, Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
        this.schedule = schedule;
        this.isFixedTime = isFixedTime;
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.title = title;
        this.remark = remark;
    }

    /**
     * Setter of {@link #schedule} field.
     * @param schedule {@link Schedule} value to set.
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
