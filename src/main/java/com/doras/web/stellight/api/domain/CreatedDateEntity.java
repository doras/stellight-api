package com.doras.web.stellight.api.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Created Date super entity class.
 * Keep track of created date time.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedDateEntity {

    /**
     * created date time
     */
    @CreatedDate
    private LocalDateTime createdDateTime;
}
