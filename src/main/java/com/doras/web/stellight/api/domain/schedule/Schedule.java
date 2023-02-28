package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.DeletedDateByEntity;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;

/**
 * Schedule entity of {@link Stellar}
 * When a schedule is created or updated, {@link ScheduleHistory} is automatically added.
 */
@Getter
@NoArgsConstructor
@Entity
public class Schedule extends DeletedDateByEntity {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * (Required) Relationship to {@link Stellar}, the owner of this Schedule
     */
    @ManyToOne
    @JoinColumn(name = "stellar_id", nullable = false)
    private Stellar stellar;

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
     * Histories of the schedule including the information of creating or updating.
     */
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ScheduleHistory> scheduleHistories = new HashSet<>();

    /**
     * Update field values. And add new {@link ScheduleHistory} with same values.
     * @param isFixedTime
     *        Value for {@link #isFixedTime}.
     *        If false, truncate {@code startDateTime} to days. That is, using only date information.
     * @param startDateTime
     *        Value for {@link #startDateTime}.
     *        If {@code isFixedTime} is false, truncate to days. That is, using only date information.
     * @param title Value for {@link #title}.
     * @param remark Value for {@link #remark}.
     */
    public void update(Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
        this.isFixedTime = isFixedTime;
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.title = title;
        this.remark = remark;

        // automatically add ScheduleHistory
        this.scheduleHistories.add(ScheduleHistory.builder()
                .schedule(this)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());
    }

    /**
     * Constructor of Schedule. Add new {@link ScheduleHistory} with same values.
     * @param stellar Value for {@link #stellar}.
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
    public Schedule(Stellar stellar, Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
        this.stellar = stellar;
        this.isFixedTime = isFixedTime;
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.title = title;
        this.remark = remark;

        // automatically create ScheduleHistory
        this.scheduleHistories.add(ScheduleHistory.builder()
                .schedule(this)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());
    }

    /**
     * Setter of {@link #stellar} field.
     * @param stellar {@link Stellar} value to set.
     */
    public void setStellar(Stellar stellar) {
        this.stellar = stellar;
    }
}
