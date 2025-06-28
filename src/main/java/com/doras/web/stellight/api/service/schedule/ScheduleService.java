package com.doras.web.stellight.api.service.schedule;

import com.doras.web.stellight.api.domain.schedule.QSchedule;
import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.exception.InvalidArgumentException;
import com.doras.web.stellight.api.exception.ScheduleNotFoundException;
import com.doras.web.stellight.api.web.dto.ScheduleFindAllRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleResponseDto;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service about {@link Schedule}.
 */
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final StellarRepository stellarRepository;
    private final ScheduleRepository scheduleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Save a new schedule with given data in {@code requestDto}.
     * @param requestDto Request DTO with data to save.
     * @return ID of saved entity.
     */
    @Transactional
    public Long save(ScheduleSaveRequestDto requestDto) {

        // find Stellar entity with given stellar id
        // When not found, throw InvalidArgumentException not StellarNotFoundException
        // because this is not a method for finding stellar.
        Stellar stellar = stellarRepository.findById(requestDto.getStellarId())
                .orElseThrow(() ->
                        new InvalidArgumentException("존재하지 않는 스텔라 id 입니다. id = " + requestDto.getStellarId()));

        // make Schedule entity with Stellar
        Schedule schedule = requestDto.toScheduleEntity();
        schedule.setStellar(stellar);
        // save Schedule
        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule.getId();
    }

    /**
     * Find Schedule by id that is not (soft) deleted.
     * @param id ID for schedule to be found.
     * @return Information of found entity in {@link ScheduleResponseDto}.
     */
    @Transactional(readOnly = true)
    public ScheduleResponseDto findById(Long id) {
        Schedule entity = scheduleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        return new ScheduleResponseDto(entity);
    }

    /**
     * Update the schedule entity with given {@code id} to data in {@code requestDto}.
     * @param id ID for schedule to be updated
     * @param requestDto DTO that has data to update
     * @return ID of updated entity
     */
    @Transactional
    public Long update(Long id, ScheduleUpdateRequestDto requestDto) {
        Schedule schedule = scheduleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        // update schedule
        schedule.update(
                requestDto.getIsFixedTime(),
                requestDto.getStartDateTime(),
                requestDto.getTitle(),
                requestDto.getRemark());

        return id;
    }

    /**
     * Delete (soft deleting) the schedule entity with given {@code id}.
     * @param id ID for schedule to be deleted
     */
    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));

        // delete schedule (soft delete)
        schedule.delete();
    }

    /**
     * Find all schedules not deleted with given filters in {@code requestDto}.
     * @param requestDto DTO that has filters
     * @return List of found entities with {@link ScheduleResponseDto} classes.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> findAllSchedules(ScheduleFindAllRequestDto requestDto, Pageable pageable) {
        QSchedule schedule = QSchedule.schedule;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(schedule.isDeleted.eq(false));

        if (requestDto.getStellarId() != null) {
            condition.and(schedule.stellar.id.in(requestDto.getStellarId()));
        }
        if (requestDto.getStartDateTimeAfter() != null || requestDto.getStartDateTimeBefore() != null) {
            condition.and(
                    schedule.startDateTime.between(
                            requestDto.getStartDateTimeAfter(), requestDto.getStartDateTimeBefore()));
        }

        List<Schedule> contents = new JPAQuery<>(entityManager)
                .select(schedule)
                .from(schedule)
                .where(condition)
                .orderBy(schedule.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count Query
        Long total = new JPAQuery<>(entityManager)
                .select(schedule.count())
                .from(schedule)
                .where(condition)
                .fetchOne();

        List<ScheduleResponseDto> dtoList = contents.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, total == null ? 0 : total);
    }
}
