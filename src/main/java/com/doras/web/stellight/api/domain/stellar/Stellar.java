package com.doras.web.stellight.api.domain.stellar;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

/**
 * Stellar entity which is VTuber from StelLive.
 */
@Getter
@NoArgsConstructor
@Entity
public class Stellar {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * (Required) Korean name
     */
    @Column(nullable = false)
    private String nameKor;

    /**
     * English name
     */
    private String nameEng;

    /**
     * Japanese name
     */
    private String nameJpn;

    /**
     * Relationship to {@link Schedule}, the schedules of this Stellar.
     */
    @OneToMany(mappedBy = "stellar", fetch = FetchType.LAZY)
    private Collection<Schedule> schedules;

    /**
     * Constructor of Schedule.
     * @param nameKor Values for {@link #nameKor}.
     * @param nameEng Values for {@link #nameEng}.
     * @param nameJpn Values for {@link #nameJpn}.
     */
    @Builder
    public Stellar(String nameKor, String nameEng, String nameJpn) {
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.nameJpn = nameJpn;
    }
}
