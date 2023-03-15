package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.exception.DataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Advice class to set exception handler of {@link DataNotFoundException}.
 */
@ControllerAdvice
public class DataNotFoundAdvice {

    /**
     * Exception handler of {@link DataNotFoundException}.
     * 404 error when the exception is thrown.
     * @param ex thrown exception
     * @return detailed message of exception
     */
    @ResponseBody
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String dataNotFoundHandler(DataNotFoundException ex) {
        return ex.getMessage();
    }
}
