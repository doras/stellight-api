package com.doras.web.stellight.api.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link ScheduleHistory}.
 */
public interface ScheduleHistoryRepository extends JpaRepository<ScheduleHistory, Long> {
}
