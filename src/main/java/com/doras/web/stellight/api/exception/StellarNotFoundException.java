package com.doras.web.stellight.api.exception;

public class StellarNotFoundException extends DataNotFoundException {
    public StellarNotFoundException(Long id) {
        super("해당 스텔라가 없습니다. id = " + id);
    }
}
