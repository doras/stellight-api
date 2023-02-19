package com.doras.web.stellight.api.exception;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(Long id) {
        super("해당 데이터가 없습니다. id = " + id);
    }
}
