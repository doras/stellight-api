package com.doras.web.stellight.api.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, QuerydslPredicateExecutor<Schedule> {
    Optional<Schedule> findByIdAndIsDeletedFalse(Long id);
}
