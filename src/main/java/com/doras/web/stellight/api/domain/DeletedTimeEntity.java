package com.doras.web.stellight.api.domain;

import lombok.Getter;

import javax.persistence.MappedSuperclass;

/**
 * Deleted time super entity class.
 * Have {@code createdDateTime}, {@code modifiedDateTime} and {@link #isDeleted}.
 * When using soft deletion, extend this class.
 */
@Getter
@MappedSuperclass
public abstract class DeletedTimeEntity extends BaseTimeEntity {

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
