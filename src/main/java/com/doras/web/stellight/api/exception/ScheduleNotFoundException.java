package com.doras.web.stellight.api.exception;

public class ScheduleNotFoundException extends DataNotFoundException {
    public ScheduleNotFoundException(Long id) {
        super("해당 스케줄이 없습니다. id = " + id);
    }
}
