package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the data is not found.
 */
public class DataNotFoundException extends RuntimeException {

    /**
     * Constructor with custom message.
     * @param message the detailed message.
     */
    public DataNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with ID that data is not found by.
     * Using default detailed message with given {@code id}.
     * @param id the ID that the data was attempted to be found but failed.
     */
    public DataNotFoundException(Long id) {
        super("해당 데이터가 없습니다. id = " + id);
    }
}
