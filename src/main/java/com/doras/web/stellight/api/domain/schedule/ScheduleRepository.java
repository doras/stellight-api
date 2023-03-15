package com.doras.web.stellight.api.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

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
}
