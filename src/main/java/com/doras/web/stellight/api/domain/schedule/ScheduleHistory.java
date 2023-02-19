package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.CreatedTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@Entity
public class ScheduleHistory extends CreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private Boolean isFixedTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String remark;

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

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
