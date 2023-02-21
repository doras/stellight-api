package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.service.schedule.ScheduleService;
import com.doras.web.stellight.api.web.dto.ScheduleFindAllRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleResponseDto;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller about schedules.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/schedules")
public class SchedulesController {

    private final ScheduleService scheduleService;

    /**
     * Save a new schedule with given data in requestDto.
     * @param requestDto request DTO with data to save
     * @return ID of saved entity
     */
    @PostMapping
    public Long save(@RequestBody ScheduleSaveRequestDto requestDto) {
        return scheduleService.save(requestDto);
    }

    /**
     * Find Schedule by id that is not (soft) deleted.
     * @param id ID for schedule to be found
     * @return information of found entity in {@link ScheduleResponseDto}
     */
    @GetMapping("/{id}")
    public ScheduleResponseDto findById(@PathVariable Long id) {
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
        return scheduleService.update(id, requestDto);
    }

    /**
     * Delete (soft deleting) the schedule entity with given {@code id}.
     * @param id ID for schedule to be deleted
     * @return ID for schedule to be deleted
     */
    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return id;
    }

    /**
     * Find all schedules with given filters in {@code requestDto}.
     * @param requestDto DTO that has filters
     * @return List of found entities with {@link ScheduleResponseDto} classes.
     */
    @GetMapping
    public List<ScheduleResponseDto> findAll(ScheduleFindAllRequestDto requestDto) {
        return scheduleService.findAllSchedules(requestDto);
    }
}
