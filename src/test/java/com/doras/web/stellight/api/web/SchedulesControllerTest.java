package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistoryRepository;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link SchedulesController}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchedulesControllerTest {

    @LocalServerPort
    private int port;

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

    /**
     * Set up mvc before each test.
     */
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Clean up entityManager after each test.
     */
    @AfterEach
    public void cleanup() {
        scheduleHistoryRepository.deleteAll();
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    /**
     * Test for saving schedule.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void saveSchedule() {

        //pre-load
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        // Because of lazy fetch about ScheduleHistory, must use additional transaction code.
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
                try {
                    mvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().registerModule(new JavaTimeModule())
                                    .writeValueAsString(requestDto)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                //then

                // schedule check
                Schedule savedSchedule = scheduleRepository.findAll().get(0);
                assertThat(savedSchedule.getStellar().getId()).isEqualTo(stellarId);
                assertThat(savedSchedule.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(savedSchedule.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(savedSchedule.getTitle()).isEqualTo(title);
                assertThat(savedSchedule.getRemark()).isEqualTo(remark);

                // schedule history check
                ScheduleHistory savedScheduleHistory = savedSchedule.getScheduleHistories().iterator().next();
                assertThat(savedScheduleHistory.getSchedule().getId()).isEqualTo(savedSchedule.getId());
                assertThat(savedScheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
                assertThat(savedScheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
                assertThat(savedScheduleHistory.getTitle()).isEqualTo(title);
                assertThat(savedScheduleHistory.getRemark()).isEqualTo(remark);
            }
        });
    }

    /**
     * Test for getting schedule
     * @throws Exception throws Exception from MockMvc
     */
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
                .andExpect(jsonPath("$.stellarId", is(savedSchedule.getStellar().getId()), Long.class))
                .andExpect(jsonPath("$.stellarNameKor", is(savedSchedule.getStellar().getNameKor())))
                .andExpect(jsonPath("$.isFixedTime", is(savedSchedule.getIsFixedTime())))
                .andExpect(jsonPath("$.startDateTime", is(savedSchedule
                        .getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.title", is(savedSchedule.getTitle())))
                .andExpect(jsonPath("$.remark", is(savedSchedule.getRemark())));

    }

    /**
     * Test for updating schedule
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updateSchedule() throws Exception {
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

        //when
        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        // Because of lazy fetch about ScheduleHistory, must use additional transaction code.
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                // schedule check
                Schedule modifiedSchedule = scheduleRepository.findAll().get(0);
                assertThat(modifiedSchedule.getIsFixedTime()).isEqualTo(expectedIsFixedTime);
                assertThat(modifiedSchedule.getStartDateTime()).isEqualTo(expectedStartDateTime);
                assertThat(modifiedSchedule.getTitle()).isEqualTo(expectedTitle);
                assertThat(modifiedSchedule.getRemark()).isEqualTo(expectedRemark);

                // histories check
                Collection<ScheduleHistory> scheduleHistories = modifiedSchedule.getScheduleHistories();
                assertThat(scheduleHistories.size()).isEqualTo(2);
                ScheduleHistory savedScheduleHistory = scheduleHistories.stream()
                        .max(Comparator.comparing(ScheduleHistory::getId)).orElseThrow();
                assertThat(savedScheduleHistory.getSchedule().getId()).isEqualTo(savedSchedule.getId());
                assertThat(savedScheduleHistory.getIsFixedTime()).isEqualTo(expectedIsFixedTime);
                assertThat(savedScheduleHistory.getStartDateTime()).isEqualTo(expectedStartDateTime);
                assertThat(savedScheduleHistory.getTitle()).isEqualTo(expectedTitle);
                assertThat(savedScheduleHistory.getRemark()).isEqualTo(expectedRemark);
            }
        });
    }

    /**
     * Test for deleting schedule
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    @WithMockUser(roles = "USER")
    public void deleteSchedule() throws Exception {
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

        Long scheduleId = savedSchedule.getId();

        LocalDateTime now = LocalDateTime.now();

        String url = "http://localhost:" + port + "/api/v1/schedules/" + scheduleId;

        //when, then
        mvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(scheduleId)));
        Schedule deletedSchedule = scheduleRepository.findById(scheduleId).orElseThrow();
        assertThat(deletedSchedule.getIsDeleted()).isEqualTo(true);
        assertThat(deletedSchedule.getModifiedDateTime()).isAfterOrEqualTo(now);
    }

    /**
     * Test for finding no element by id after deletion
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void notFoundByIdAfterDelete() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule schedule = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build();
        schedule.delete();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        String url = "http://localhost:" + port + "/api/v1/schedules/" + savedSchedule.getId();

        //when, then
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    /**
     * Test for finding no elements all after deletion
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void notFoundAllAfterDelete() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule schedule = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build();
        schedule.delete();

        scheduleRepository.save(schedule);

        String url = "http://localhost:" + port + "/api/v1/schedules";

        //when, then
        mvc.perform(get(url))
                // status check
                .andExpect(status().isOk())
                // list length check
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test for failure about updating after deletion
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    @WithMockUser(roles = "USER")
    public void notUpdateAfterDelete() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule schedule = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build();
        schedule.delete();
        Schedule savedSchedule = scheduleRepository.save(schedule);

        String url = "http://localhost:" + port + "/api/v1/schedules/" + savedSchedule.getId();

        ScheduleUpdateRequestDto requestDto = ScheduleUpdateRequestDto.builder()
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 18, 21, 0))
                .title("수정된 스케줄의 이름")
                .remark("수정된 스케줄에 대한 비고")
                .build();

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String updatedScheduleJson = mapper.writeValueAsString(requestDto);

        //when, then
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedScheduleJson))
                .andExpect(status().isNotFound());
    }

    /**
     * Test for getting no schedules
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void getNoSchedules() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/schedules";

        //when, then
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Test for getting all schedules
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void getAllSchedules() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());
        Schedule savedSchedule2 = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 16, 0, 0, 0))
                .title("스케줄의 이름2")
                .remark("스케줄에 대한 비고2")
                .build());

        String url = "http://localhost:" + port + "/api/v1/schedules";

        //when, then
        mvc.perform(get(url))
                // status check
                .andExpect(status().isOk())
                // list length check
                .andExpect(jsonPath("$.length()").value(2))
                // first element check
                .andExpect(jsonPath("$[0].id", is(savedSchedule.getId()), Long.class))
                .andExpect(jsonPath("$[0].stellarId", is(savedSchedule.getStellar().getId()), Long.class))
                .andExpect(jsonPath("$[0].stellarNameKor", is(savedSchedule.getStellar().getNameKor())))
                .andExpect(jsonPath("$[0].isFixedTime", is(savedSchedule.getIsFixedTime())))
                .andExpect(jsonPath("$[0].startDateTime", is(
                        savedSchedule.getStartDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[0].title", is(savedSchedule.getTitle())))
                .andExpect(jsonPath("$[0].remark", is(savedSchedule.getRemark())))
                // second element check
                .andExpect(jsonPath("$[1].id", is(savedSchedule2.getId()), Long.class))
                .andExpect(jsonPath("$[1].stellarId", is(savedSchedule2.getStellar().getId()), Long.class))
                .andExpect(jsonPath("$[1].stellarNameKor", is(savedSchedule2.getStellar().getNameKor())))
                .andExpect(jsonPath("$[1].isFixedTime", is(savedSchedule2.getIsFixedTime())))
                .andExpect(jsonPath("$[1].startDateTime", is(
                        savedSchedule2.getStartDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$[1].title", is(savedSchedule2.getTitle())))
                .andExpect(jsonPath("$[1].remark", is(savedSchedule2.getRemark())));
    }

    /**
     * Test for getting all schedules with filters
     * @throws Exception throws Exception from MockMvc
     */
    @Test
    public void getAllSchedulesWithFilters() throws Exception {
        //given
        Stellar stellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Stellar stellar2 = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름2")
                .nameEng("english name2")
                .nameJpn("日本語の名前2")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());
        Schedule savedSchedule2 = scheduleRepository.save(Schedule.builder()
                .stellar(stellar2)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 16, 0, 0, 0))
                .title("스케줄의 이름2")
                .remark("스케줄에 대한 비고2")
                .build());

        String url = "http://localhost:" + port + "/api/v1/schedules";

        //when, then

        // stellarId
        mvc.perform(get(url)
                        .param("stellarId", String.valueOf(stellar.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(savedSchedule.getId()), Long.class));

        // stellarIds
        String stellarIds = Stream.of(stellar.getId(), stellar2.getId())
                .map(String::valueOf).collect(Collectors.joining(","));
        mvc.perform(get(url)
                        .param("stellarId", stellarIds))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id", is(savedSchedule.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(savedSchedule2.getId()), Long.class));

        // date after
        mvc.perform(get(url)
                        .param("startDateTimeAfter", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(savedSchedule2.getId()), Long.class));

        // date before
        mvc.perform(get(url)
                        .param("startDateTimeBefore", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(savedSchedule.getId()), Long.class));

        // all filters
        mvc.perform(get(url)
                        .param("stellarId", String.valueOf(stellar.getId()))
                        .param("startDateTimeAfter", "2023-02-14T00:00:00")
                        .param("startDateTimeBefore", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(savedSchedule.getId()), Long.class));
    }
}
