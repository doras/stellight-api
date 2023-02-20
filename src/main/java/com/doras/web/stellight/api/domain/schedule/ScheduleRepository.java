package com.doras.web.stellight.api.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByIdAndIsDeletedFalse(Long id);
}
