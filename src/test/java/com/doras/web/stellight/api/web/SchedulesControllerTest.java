package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistoryRepository;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchedulesControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StellarRepository stellarRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleHistoryRepository scheduleHistoryRepository;


    @AfterEach
    public void tearDown() {
        scheduleHistoryRepository.deleteAll();
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    @Test
    public void saveSchedule() {

        String nameKor = "한국 이름";
        String nameEng = "english name";
        String nameJpn = "日本語の名前";

        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor(nameKor)
                .nameEng(nameEng)
                .nameJpn(nameJpn)
                .build());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //given
                Long stellarId = savedStellar.getId();

                Boolean isFixedTime = true;
                LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 19, 0);
                String title = "스케줄의 이름";
                String remark = "스케줄에 대한 비고";

                ScheduleSaveRequestDto requestDto = ScheduleSaveRequestDto.builder()
                        .stellarId(stellarId)
                        .isFixedTime(isFixedTime)
                        .startDateTime(startDateTime)
                        .title(title)
                        .remark(remark)
                        .build();

                String url = "http://localhost:" + port + "/api/v1/schedules";

                //when
                ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

                //then
                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(responseEntity.getBody()).isGreaterThan(0L);

                Schedule savedSchedule = scheduleRepository.findAll().get(0);
                assertThat(savedSchedule.getStellar().getId()).isEqualTo(stellarId);
                assertThat(savedSchedule.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(savedSchedule.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(savedSchedule.getTitle()).isEqualTo(title);
                assertThat(savedSchedule.getRemark()).isEqualTo(remark);

                ScheduleHistory savedScheduleHistory = savedSchedule.getScheduleHistories().iterator().next();
                assertThat(savedScheduleHistory.getSchedule().getId()).isEqualTo(savedSchedule.getId());
                assertThat(savedScheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(savedScheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(savedScheduleHistory.getTitle()).isEqualTo(title);
                assertThat(savedScheduleHistory.getRemark()).isEqualTo(remark);
            }
        });
    }
}
