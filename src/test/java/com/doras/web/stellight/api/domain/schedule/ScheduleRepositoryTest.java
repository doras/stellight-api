package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ScheduleRepository}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ScheduleRepositoryTest {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    StellarRepository stellarRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    /**
     * Clean up all repositories after each test.
     */
    @AfterEach
    public void cleanup() {
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    /**
     * Test for saving schedule.
     */
    @Test
    public void saveSchedule() {
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

        // Because of lazy fetch about ScheduleHistory, must use additional transaction code.
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //when
                List<Schedule> scheduleList = scheduleRepository.findAll();

                //then
                Schedule scheduleFound = scheduleList.get(0);
                assertThat(scheduleFound.getStellar().getId()).isEqualTo(stellar.getId());
                assertThat(scheduleFound.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(scheduleFound.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(scheduleFound.getTitle()).isEqualTo(title);
                assertThat(scheduleFound.getRemark()).isEqualTo(remark);

                Collection<ScheduleHistory> scheduleHistories = scheduleFound.getScheduleHistories();
                assertThat(scheduleHistories.size()).isEqualTo(1);
                ScheduleHistory savedScheduleHistory = scheduleHistories.iterator().next();
                assertThat(savedScheduleHistory.getSchedule().getId()).isEqualTo(scheduleFound.getId());
                assertThat(savedScheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(savedScheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(savedScheduleHistory.getTitle()).isEqualTo(title);
                assertThat(savedScheduleHistory.getRemark()).isEqualTo(remark);
            }
        });
    }

    /**
     * Test for saving BaseTimeEntity information in Schedule.
     */
    @Test
    public void saveBaseTimeEntity() {
        //given
        LocalDateTime now = LocalDateTime.of(2023, 2, 20, 0, 0, 0);

        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("테스트 한국어")
                .nameEng("test english")
                .nameJpn("テストの日本語")
                .build());

        scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        //when
        List<Schedule> scheduleList = scheduleRepository.findAll();

        //then
        Schedule schedule = scheduleList.get(0);

        assertThat(schedule.getCreatedDateTime()).isAfter(now);
        assertThat(schedule.getModifiedDateTime()).isAfter(now);
    }
}
