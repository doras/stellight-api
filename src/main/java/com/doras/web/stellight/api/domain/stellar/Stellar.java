package com.doras.web.stellight.api.domain.stellar;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

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

    @OneToMany(mappedBy = "stellar", fetch = FetchType.LAZY)
    private Collection<Schedule> schedules;

    @Builder
    public Stellar(String nameKor, String nameEng, String nameJpn) {
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.nameJpn = nameJpn;
    }
}
