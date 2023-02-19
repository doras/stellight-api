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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ScheduleRepositoryTest {

    @Autowired
    StellarRepository stellarRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @AfterEach
    public void cleanup() {
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    @Test
    public void saveFixedSchedule() {
        //given
        String nameKor = "테스트 한국어";
        String nameEng = "test english";
        String nameJpn = "テストの日本語";

        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());

        Boolean isFixedTime = true;
        LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 19, 0);
        String title = "스케줄의 이름";
        String remark = "스케줄에 대한 비고";

        scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());

        //when
        List<Schedule> scheduleList = scheduleRepository.findAll();

        //then
        Schedule schedule = scheduleList.get(0);
        assertThat(schedule.getStellar().getId()).isEqualTo(stellar.getId());
        assertThat(schedule.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(schedule.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(schedule.getTitle()).isEqualTo(title);
        assertThat(schedule.getRemark()).isEqualTo(remark);
    }

    @Test
    public void saveNotFixedSchedule() {
        //given
        String nameKor = "테스트 한국어";
        String nameEng = "test english";
        String nameJpn = "テストの日本語";

        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());

        Boolean isFixedTime = false;
        LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 19, 0);
        String title = "스케줄의 이름";
        String remark = "스케줄에 대한 비고";

        scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());

        //when
        List<Schedule> scheduleList = scheduleRepository.findAll();

        //then
        Schedule schedule = scheduleList.get(0);
        assertThat(schedule.getStellar().getId()).isEqualTo(stellar.getId());
        assertThat(schedule.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(schedule.getStartDateTime()).isEqualTo(startDateTime.truncatedTo(ChronoUnit.DAYS));
        assertThat(schedule.getTitle()).isEqualTo(title);
        assertThat(schedule.getRemark()).isEqualTo(remark);
    }
}
