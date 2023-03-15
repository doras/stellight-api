package com.doras.web.stellight.api.domain;

import lombok.Getter;

import javax.persistence.MappedSuperclass;

/**
 * Deleted Date and ByUser super entity class.
 * Have whether the entity is deleted or not as well as auditing information of {@link BaseDateByEntity}.
 * When using soft deletion, extend this class.
 */
@Getter
@MappedSuperclass
public abstract class DeletedDateByEntity extends BaseDateByEntity {

    /**
     * Soft deletion flag.
     */
    private Boolean isDeleted = false;

    /**
     * Soft delete this object.
     * Not real deletion in database.
     */
    public void delete() {
        this.isDeleted = true;
    }
}
