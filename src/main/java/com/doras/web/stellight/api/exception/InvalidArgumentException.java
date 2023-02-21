package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the invalid argument is given.
 */
public class InvalidArgumentException extends RuntimeException {

    /**
     * Constructor with custom message.
     * @param message the detailed message.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
