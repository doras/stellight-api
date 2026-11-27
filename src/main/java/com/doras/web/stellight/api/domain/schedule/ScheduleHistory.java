package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.CreatedDateByEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * History entity for {@link Schedule}.
 * Track creation or modification history for {@link Schedule}.
 */
@Getter
@NoArgsConstructor
@Entity
public class ScheduleHistory extends CreatedDateByEntity {

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
     * (Required) Whether the schedule is a day off (휴방) or not. A day off has no fixed time.
     */
    @Column(nullable = false)
    private Boolean isDayOff;

    /**
     * (Required) Date and time when the schedule starts.
     * If {@link #isFixedTime} is false, time information is not used.
     */
    @Column(nullable = false)
    private LocalDateTime startDateTime;

    /**
     * End of the expected start window, when the exact start time is not certain yet.
     * The window begins at {@link #startDateTime}. Not necessary.
     */
    private LocalDateTime startDateTimeUntil;

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
     * @param isDayOff Value for {@link #isDayOff}. Null is regarded as false.
     * @param startDateTime
     *        Value for {@link #startDateTime}.
     *        If {@code isFixedTime} is false, truncate to days. That is, using only date information.
     * @param startDateTimeUntil Value for {@link #startDateTimeUntil}.
     * @param title Value for {@link #title}.
     * @param remark Value for {@link #remark}.
     */
    @Builder
    public ScheduleHistory(Schedule schedule, Boolean isFixedTime, Boolean isDayOff, LocalDateTime startDateTime,
                           LocalDateTime startDateTimeUntil, String title, String remark) {
        this.schedule = schedule;
        this.isFixedTime = isFixedTime;
        this.isDayOff = Boolean.TRUE.equals(isDayOff);
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.startDateTimeUntil = startDateTimeUntil;
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
