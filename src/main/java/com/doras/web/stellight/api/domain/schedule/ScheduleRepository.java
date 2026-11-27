package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JPA repository for {@link Schedule} with querydsl.
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, QuerydslPredicateExecutor<Schedule> {

    /**
     * Find schedule by id which is not (soft) deleted.
     * @param id ID of schedule.
     * @return Optional object for found schedule object.
     */
    Optional<Schedule> findByIdAndIsDeletedFalse(Long id);

    /**
     * Check whether a day-off schedule which is not (soft) deleted exists for the stellar on the date.
     * @param stellar Owner of the schedule.
     * @param startDateTime Date of the day off. A day off has no fixed time, so it is the start of the day.
     * @return true if exists.
     */
    boolean existsByStellarAndStartDateTimeAndIsDayOffTrueAndIsDeletedFalse(
            Stellar stellar, LocalDateTime startDateTime);

    /**
     * Same as {@link #existsByStellarAndStartDateTimeAndIsDayOffTrueAndIsDeletedFalse(Stellar, LocalDateTime)},
     * excluding the schedule with {@code id}.
     * @param stellar Owner of the schedule.
     * @param startDateTime Date of the day off. A day off has no fixed time, so it is the start of the day.
     * @param id ID of the schedule to exclude.
     * @return true if exists.
     */
    boolean existsByStellarAndStartDateTimeAndIsDayOffTrueAndIsDeletedFalseAndIdNot(
            Stellar stellar, LocalDateTime startDateTime, Long id);
}
