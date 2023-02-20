package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.domain.CreatedTimeEntity;
import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistoryRepository;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private StellarRepository stellarRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleHistoryRepository scheduleHistoryRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @AfterEach
    public void tearDown() {
        scheduleHistoryRepository.deleteAll();
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    @Test
    public void saveSchedule() {

        //pre-load
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
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

    @Test
    public void getSchedule() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        String url = "http://localhost:" + port + "/api/v1/schedules/" + savedSchedule.getId();

        //when, then
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stellarNameKor", is(savedSchedule.getStellar().getNameKor())))
                .andExpect(jsonPath("$.isFixedTime", is(savedSchedule.getIsFixedTime())))
                .andExpect(jsonPath("$.startDateTime", is(savedSchedule
                        .getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.title", is(savedSchedule.getTitle())))
                .andExpect(jsonPath("$.remark", is(savedSchedule.getRemark())));

    }

    @Test
    public void updateSchedule() {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        String url = "http://localhost:" + port + "/api/v1/schedules/" + savedSchedule.getId();

        Boolean expectedIsFixedTime = true;
        LocalDateTime expectedStartDateTime = LocalDateTime.of(2023, 2, 18, 21, 0);
        String expectedTitle = "수정된 스케줄의 이름";
        String expectedRemark = "수정된 스케줄에 대한 비고";

        ScheduleUpdateRequestDto requestDto = ScheduleUpdateRequestDto.builder()
                .isFixedTime(expectedIsFixedTime)
                .startDateTime(expectedStartDateTime)
                .title(expectedTitle)
                .remark(expectedRemark)
                .build();

        HttpEntity<ScheduleUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(responseEntity.getBody()).isGreaterThan(0L);

                Schedule modifiedSchedule = scheduleRepository.findAll().get(0);
                assertThat(modifiedSchedule.getIsFixedTime()).isEqualTo(expectedIsFixedTime);
                assertThat(modifiedSchedule.getStartDateTime()).isEqualTo(expectedStartDateTime);
                assertThat(modifiedSchedule.getTitle()).isEqualTo(expectedTitle);
                assertThat(modifiedSchedule.getRemark()).isEqualTo(expectedRemark);

                Collection<ScheduleHistory> scheduleHistories = modifiedSchedule.getScheduleHistories();
                assertThat(scheduleHistories.size()).isEqualTo(2);
                ScheduleHistory savedScheduleHistory = scheduleHistories.stream()
                        .max(Comparator.comparing(CreatedTimeEntity::getCreatedDateTime)).orElseThrow();
                assertThat(savedScheduleHistory.getSchedule().getId()).isEqualTo(savedSchedule.getId());
                assertThat(savedScheduleHistory.getIsFixedTime()).isEqualTo(expectedIsFixedTime);
                assertThat(savedScheduleHistory.getStartDateTime()).isEqualTo(expectedStartDateTime);
                assertThat(savedScheduleHistory.getTitle()).isEqualTo(expectedTitle);
                assertThat(savedScheduleHistory.getRemark()).isEqualTo(expectedRemark);
            }
        });
    }
}
