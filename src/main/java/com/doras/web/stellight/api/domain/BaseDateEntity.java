package com.doras.web.stellight.api.domain;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Base date super entity class.
 * Have {@code createdDateTime} and {@link #modifiedDateTime}.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDateEntity extends CreatedDateEntity {

    @LastModifiedDate
    private LocalDateTime modifiedDateTime;

}
