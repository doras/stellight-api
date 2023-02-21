package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.exception.InvalidArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Advice class to set exception handler of {@link InvalidArgumentException}.
 */
@ControllerAdvice
public class InvalidArgumentAdvice {

    /**
     * Exception handler of {@link InvalidArgumentException}.
     * 422 error when the exception is thrown.
     * @param ex thrown exception
     * @return detailed message of exception
     */
    @ResponseBody
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String invalidArgumentHandler(InvalidArgumentException ex) {
        return ex.getMessage();
    }
}
