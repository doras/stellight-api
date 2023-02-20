package com.doras.web.stellight.api.domain;

import lombok.Getter;

import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
public abstract class DeletedTimeEntity extends BaseTimeEntity {
    private Boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
    }
}
