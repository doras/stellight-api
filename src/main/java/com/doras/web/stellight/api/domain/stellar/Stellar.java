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
     * A generation number that the stellar belongs to
     */
    @Column(nullable = false)
    private Byte generation;

    /**
     * Order of debut in same generation
     */
    private Byte debutOrder;

    /**
     * Personal color in 6-digit hex format
     */
    @Column(length = 6)
    private String personalColor;

    /**
     * Relationship to {@link Schedule}, the schedules of this Stellar.
     */
    @OneToMany(mappedBy = "stellar", fetch = FetchType.LAZY)
    private Collection<Schedule> schedules;

    /**
     * Constructor of Schedule.
     */
    @Builder
    public Stellar(String nameKor, String nameEng, String nameJpn, Byte generation, Byte debutOrder, String personalColor) {
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.nameJpn = nameJpn;
        this.generation = generation;
        this.debutOrder = debutOrder;
        this.personalColor = personalColor;
    }
}
