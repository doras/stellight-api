package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.service.schedule.ScheduleService;
import com.doras.web.stellight.api.web.dto.ScheduleFindAllRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleResponseDto;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller about schedules.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/schedules")
public class SchedulesController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduleService scheduleService;

    /**
     * Save a new schedule with given data in requestDto.
     * @param requestDto request DTO with data to save
     * @return ID of saved entity
     */
    @PostMapping
    public Long save(@RequestBody ScheduleSaveRequestDto requestDto) {
        logger.info("save schedule");
        return scheduleService.save(requestDto);
    }

    /**
     * Find Schedule by id that is not (soft) deleted.
     * @param id ID for schedule to be found
     * @return information of found entity in {@link ScheduleResponseDto}
     */
    @GetMapping("/{id}")
    public ScheduleResponseDto findById(@PathVariable Long id) {
        logger.info("find schedule by id = {}", id);
        return scheduleService.findById(id);
    }

    /**
     * Update the schedule entity with given id to data in requestDto.
     * @param id ID for schedule to be updated
     * @param requestDto DTO that has data to update
     * @return ID of updated entity
     */
    @PutMapping("/{id}")
    public Long update(@PathVariable Long id, @RequestBody ScheduleUpdateRequestDto requestDto) {
        logger.info("update schedule id = {}", id);
        return scheduleService.update(id, requestDto);
    }

    /**
     * Delete (soft deleting) the schedule entity with given {@code id}.
     * @param id ID for schedule to be deleted
     * @return ID for schedule to be deleted
     */
    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        logger.info("delete schedule id = {}", id);
        scheduleService.delete(id);
        return id;
    }

    /**
     * Find all schedules with pagination and filters provided in {@code requestDto}.
     * @param pageable pagination information including page number, size, and sorting
     * @param requestDto DTO containing filter criteria for schedules
     * @return Page of schedules wrapped in {@link ScheduleResponseDto}.
     */
    @GetMapping
    public Page<ScheduleResponseDto> findAll(
            @PageableDefault(page = 0, size = 10)
            Pageable pageable,
            ScheduleFindAllRequestDto requestDto
    ) {
        logger.info("find all schedules");
        return scheduleService.findAllSchedules(requestDto, pageable);
    }
}
