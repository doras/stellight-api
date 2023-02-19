package com.doras.web.stellight.api.service.schedule;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistoryRepository;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final StellarRepository stellarRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleHistoryRepository scheduleHistoryRepository;

    @Transactional
    public Long save(ScheduleSaveRequestDto requestDto) {

        // find Stellar entity
        Stellar stellar = stellarRepository.findById(requestDto.getStellarId())
                .orElseThrow(() -> new EntityNotFoundException("해당 스텔라가 없습니다. id=" + requestDto.getStellarId()));

        // make Schedule entity with Stellar
        Schedule schedule = requestDto.toScheduleEntity();
        schedule.setStellar(stellar);
        // save Schedule
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // make ScheduleHistory entity with Schedule
        ScheduleHistory scheduleHistory = requestDto.toScheduleHistoryEntity();
        scheduleHistory.setSchedule(savedSchedule);
        // save ScheduleHistory
        scheduleHistoryRepository.save(scheduleHistory);

        return savedSchedule.getId();
    }
}
