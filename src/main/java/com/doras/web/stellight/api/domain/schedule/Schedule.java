package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.DeletedTimeEntity;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

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

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    private Collection<ScheduleHistory> scheduleHistories;

    @Builder
    public Schedule(Stellar stellar, Boolean isFixedTime, LocalDateTime startDateTime, String title, String remark) {
        this.stellar = stellar;
        this.isFixedTime = isFixedTime;
        this.startDateTime = startDateTime;
        this.title = title;
        this.remark = remark;
    }

    public void setStellar(Stellar stellar) {
        this.stellar = stellar;
    }
}
