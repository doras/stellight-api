package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.exception.DataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DataNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String dataNotFoundHandler(DataNotFoundException ex) {
        return ex.getMessage();
    }
}
