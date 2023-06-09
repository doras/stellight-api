package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.service.schedule.ScheduleHistoryService;
import com.doras.web.stellight.api.web.dto.ScheduleHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller about schedule histories.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/schedule-histories")
public class ScheduleHistoriesController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduleHistoryService scheduleHistoryService;

    /**
     * Find all schedule histories.
     * @param pageable pagination and sort information given by user or default
     * @return A page of found entities with {@link ScheduleHistoryResponseDto} classes.
     */
    @GetMapping
    public Page<ScheduleHistoryResponseDto> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        logger.info("find all schedule histories");
        return scheduleHistoryService.findAllScheduleHistories(pageable);
    }
}
