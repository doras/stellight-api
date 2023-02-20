package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.DeletedTimeEntity;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;

@Getter
@NoArgsConstructor
@Entity
public class Schedule extends DeletedTimeEntity {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK for Stellar, the owner of this Schedule
     */
    @ManyToOne
    @JoinColumn(name = "stellar_id", nullable = false)
    private Stellar stellar;

    /**
     * Whether the specific time of schedule is fixed or not.
     * If not, use only Date information in {@link #startDateTime} field.
     */
    @Column(nullable = false)
    private Boolean isFixedTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String remark;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ScheduleHistory> scheduleHistories = new HashSet<>();

    public void update(Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
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

    public void setStellar(Stellar stellar) {
        this.stellar = stellar;
    }
}
