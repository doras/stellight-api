package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.exception.InvalidArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidArgumentAdvice {
    @ResponseBody
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String invalidArgumentHandler(InvalidArgumentException ex) {
        return ex.getMessage();
    }
}
