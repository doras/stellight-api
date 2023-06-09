package com.doras.web.stellight.api.service.schedule;

import com.doras.web.stellight.api.domain.schedule.ScheduleHistoryRepository;
import com.doras.web.stellight.api.web.dto.ScheduleHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service about {@link com.doras.web.stellight.api.domain.schedule.ScheduleHistory}.
 */
@RequiredArgsConstructor
@Service
public class ScheduleHistoryService {

    private final ScheduleHistoryRepository scheduleHistoryRepository;

    /**
     * Find all schedule histories with given pageable info in {@code pageable}.
     * @param pageable pagination and sort information given by user or default
     * @return A page of found entities with {@link ScheduleHistoryResponseDto} classes.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleHistoryResponseDto> findAllScheduleHistories(Pageable pageable) {
        return scheduleHistoryRepository.findAll(pageable).map(ScheduleHistoryResponseDto::new);
    }
}
