package com.doras.web.stellight.api.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Created Date and ByUser super entity class.
 * Keep track of created date time and user by whom created.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedDateByEntity {

    /**
     * created date time
     */
    @CreatedDate
    private LocalDateTime createdDateTime;

    /**
     * email address of user by whom created
     */
    @CreatedBy
    private String createdBy;
}
