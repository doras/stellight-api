package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.DeletedDateByEntity;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.exception.InvalidArgumentException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.Duration;
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
     * Maximum length of the expected start window ({@link #startDateTime} ~ {@link #startDateTimeUntil}).
     */
    public static final Duration MAX_START_WINDOW = Duration.ofHours(12);

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
     * Only used when {@link #isFixedTime} is true.
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
     * Histories of the schedule including the information of creating or updating.
     */
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ScheduleHistory> scheduleHistories = new HashSet<>();

    /**
     * Update field values. And add new {@link ScheduleHistory} with same values.
     * @param isFixedTime
     *        Value for {@link #isFixedTime}.
     *        If false, truncate {@code startDateTime} to days. That is, using only date information.
     * @param isDayOff
     *        Value for {@link #isDayOff}. If true, {@code isFixedTime} must be false.
     *        Null keeps the current value, so that clients unaware of the field don't clear it,
     *        except with a fixed time, which is never a day off (so such clients can still turn a day off
     *        into a stream).
     * @param startDateTime
     *        Value for {@link #startDateTime}.
     *        If {@code isFixedTime} is false, truncate to days. That is, using only date information.
     * @param startDateTimeUntil Value for {@link #startDateTimeUntil}.
     *        Only allowed with a fixed time, later than {@code startDateTime} by at most {@link #MAX_START_WINDOW}.
     * @param title Value for {@link #title}.
     * @param remark Value for {@link #remark}.
     * @throws InvalidArgumentException
     *         when {@code isDayOff} or {@code startDateTimeUntil} is invalid. Nothing is changed then.
     */
    public void update(Boolean isFixedTime, Boolean isDayOff, LocalDateTime startDateTime,
                       LocalDateTime startDateTimeUntil, String title, String remark) {
        if (isDayOff == null) {
            isDayOff = !Boolean.TRUE.equals(isFixedTime) && Boolean.TRUE.equals(this.isDayOff);
        }
        validate(isFixedTime, isDayOff, startDateTime, startDateTimeUntil);

        this.isFixedTime = isFixedTime;
        this.isDayOff = isDayOff;
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.startDateTimeUntil = startDateTimeUntil;
        this.title = title;
        this.remark = remark;

        // automatically add ScheduleHistory
        this.scheduleHistories.add(ScheduleHistory.builder()
                .schedule(this)
                .isFixedTime(isFixedTime)
                .isDayOff(isDayOff)
                .startDateTime(startDateTime)
                .startDateTimeUntil(startDateTimeUntil)
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
     * @param isDayOff
     *        Value for {@link #isDayOff}. Null is regarded as false. If true, {@code isFixedTime} must be false.
     * @param startDateTime
     *        Value for {@link #startDateTime}.
     *        If {@code isFixedTime} is false, truncate to days. That is, using only date information.
     * @param startDateTimeUntil Value for {@link #startDateTimeUntil}.
     *        Only allowed with a fixed time, later than {@code startDateTime} by at most {@link #MAX_START_WINDOW}.
     * @param title Value for {@link #title}.
     * @param remark Value for {@link #remark}.
     * @throws InvalidArgumentException when {@code isDayOff} or {@code startDateTimeUntil} is invalid.
     */
    @Builder
    public Schedule(Stellar stellar, Boolean isFixedTime, Boolean isDayOff, LocalDateTime startDateTime,
                    LocalDateTime startDateTimeUntil, String title, String remark) {
        isDayOff = Boolean.TRUE.equals(isDayOff);
        validate(isFixedTime, isDayOff, startDateTime, startDateTimeUntil);

        this.stellar = stellar;
        this.isFixedTime = isFixedTime;
        this.isDayOff = isDayOff;
        if (!isFixedTime) {
            this.startDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else {
            this.startDateTime = startDateTime;
        }
        this.startDateTimeUntil = startDateTimeUntil;
        this.title = title;
        this.remark = remark;

        // automatically create ScheduleHistory
        this.scheduleHistories.add(ScheduleHistory.builder()
                .schedule(this)
                .isFixedTime(isFixedTime)
                .isDayOff(isDayOff)
                .startDateTime(startDateTime)
                .startDateTimeUntil(startDateTimeUntil)
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

    /**
     * Validate the values of a schedule.
     * <ul>
     *     <li>A day off has no fixed time.</li>
     *     <li>The expected start window is optional; when {@code startDateTimeUntil} is given, the time must be
     *     fixed, and it must be later than {@code startDateTime} by at most {@link #MAX_START_WINDOW}.</li>
     * </ul>
     * @param isFixedTime Whether the time of schedule is fixed or not.
     * @param isDayOff Whether the schedule is a day off or not. Not null.
     * @param startDateTime Beginning of the window.
     * @param startDateTimeUntil End of the window. Not validated when null.
     * @throws InvalidArgumentException when the values are invalid.
     */
    private static void validate(Boolean isFixedTime, boolean isDayOff, LocalDateTime startDateTime,
                                 LocalDateTime startDateTimeUntil) {
        if (isDayOff && Boolean.TRUE.equals(isFixedTime)) {
            throw new InvalidArgumentException("휴방 일정은 시간 미정이어야 합니다.");
        }
        if (startDateTimeUntil == null) {
            return;
        }
        if (!Boolean.TRUE.equals(isFixedTime)) {
            throw new InvalidArgumentException("시간 미정인 일정에는 예정 시각을 지정할 수 없습니다.");
        }
        if (startDateTime == null || !startDateTimeUntil.isAfter(startDateTime)) {
            throw new InvalidArgumentException("예정 시각은 시작 시각보다 늦어야 합니다.");
        }
        if (Duration.between(startDateTime, startDateTimeUntil).compareTo(MAX_START_WINDOW) > 0) {
            throw new InvalidArgumentException(
                    "예정 시각은 시작 시각으로부터 " + MAX_START_WINDOW.toHours() + "시간 이내여야 합니다.");
        }
    }
}
