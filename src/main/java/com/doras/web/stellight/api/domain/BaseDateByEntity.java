package com.doras.web.stellight.api.domain;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Base Date and ByUser super entity class.
 * Keep track of created and modified date time and users by whom created and modified.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDateByEntity extends CreatedDateByEntity {

    /**
     * Last modified date time
     */
    @LastModifiedDate
    private LocalDateTime modifiedDateTime;

    /**
     * email address of user by whom last modified
     */
    @LastModifiedBy
    private String modifiedBy;
}
