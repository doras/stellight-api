package com.doras.web.stellight.api.exception;

/**
 * Unchecked exception thrown when the {@link com.doras.web.stellight.api.domain.schedule.Schedule} data is not found.
 */
public class ScheduleNotFoundException extends DataNotFoundException {

    /**
     * Constructor with ID that data is not found by.
     * Using default detailed message of {@link ScheduleNotFoundException} with given {@code id}.
     * @param id the ID that the data was attempted to be found but failed.
     */
    public ScheduleNotFoundException(Long id) {
        super("해당 스케줄이 없습니다. id = " + id);
    }
}
