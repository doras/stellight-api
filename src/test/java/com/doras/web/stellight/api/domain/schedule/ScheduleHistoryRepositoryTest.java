package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ScheduleHistoryRepositoryTest {

    @Autowired
    StellarRepository stellarRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleHistoryRepository scheduleHistoryRepository;

    @AfterEach
    public void cleanup() {
        scheduleHistoryRepository.deleteAll();
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    @Test
    public void saveScheduleHistory() {
        //given
        String nameKor = "테스트 한국어";
        String nameEng = "test english";
        String nameJpn = "テストの日本語";

        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());

        Schedule schedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 2, 5, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        Boolean isFixedTime = true;
        LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 19, 0);
        String title = "스케줄 히스토리의 이름";
        String remark = "스케줄 히스토리에 대한 비고";

        scheduleHistoryRepository.save(ScheduleHistory.builder()
                .schedule(schedule)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());

        //when
        List<ScheduleHistory> scheduleHistoryList = scheduleHistoryRepository.findAll();

        //then
        assertThat(scheduleHistoryList.size()).isEqualTo(2);
        ScheduleHistory scheduleHistory = scheduleHistoryList.stream()
                .max(Comparator.comparing(ScheduleHistory::getId)).orElseThrow();
        assertThat(scheduleHistory.getSchedule().getId()).isEqualTo(schedule.getId());
        assertThat(scheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(scheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(scheduleHistory.getTitle()).isEqualTo(title);
        assertThat(scheduleHistory.getRemark()).isEqualTo(remark);
    }
}
