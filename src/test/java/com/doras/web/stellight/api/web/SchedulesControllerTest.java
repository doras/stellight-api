package com.doras.web.stellight.api.web;

import com.doras.web.stellight.api.domain.schedule.Schedule;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
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
    private WebApplicationContext context;

    @Autowired
    private JsonMapper jsonMapper;

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
    public void saveSchedule() throws Exception {

        //pre-load
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build());

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
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        Schedule savedSchedule = scheduleRepository.findAll().get(0);
        assertThat(savedSchedule.getStellar().getId()).isEqualTo(stellarId);
        assertThat(savedSchedule.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(savedSchedule.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(savedSchedule.getTitle()).isEqualTo(title);
        assertThat(savedSchedule.getRemark()).isEqualTo(remark);
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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
                .content(jsonMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        Schedule modifiedSchedule = scheduleRepository.findAll().get(0);
        assertThat(modifiedSchedule.getIsFixedTime()).isEqualTo(expectedIsFixedTime);
        assertThat(modifiedSchedule.getStartDateTime()).isEqualTo(expectedStartDateTime);
        assertThat(modifiedSchedule.getTitle()).isEqualTo(expectedTitle);
        assertThat(modifiedSchedule.getRemark()).isEqualTo(expectedRemark);
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        Long scheduleId = savedSchedule.getId();

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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
                .andExpect(jsonPath("$.totalElements").value(0));
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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

        String updatedScheduleJson = jsonMapper.writeValueAsString(requestDto);

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
                .andExpect(jsonPath("$.totalElements").value(0));
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
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
                .andExpect(jsonPath("$.totalElements").value(2))
                // first element check
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule.getId()), Long.class))
                .andExpect(jsonPath("$.content[0].stellarId", is(savedSchedule.getStellar().getId()), Long.class))
                .andExpect(jsonPath("$.content[0].stellarNameKor", is(savedSchedule.getStellar().getNameKor())))
                .andExpect(jsonPath("$.content[0].isFixedTime", is(savedSchedule.getIsFixedTime())))
                .andExpect(jsonPath("$.content[0].startDateTime", is(
                        savedSchedule.getStartDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.content[0].title", is(savedSchedule.getTitle())))
                .andExpect(jsonPath("$.content[0].remark", is(savedSchedule.getRemark())))
                // second element check
                .andExpect(jsonPath("$.content[1].id", is(savedSchedule2.getId()), Long.class))
                .andExpect(jsonPath("$.content[1].stellarId", is(savedSchedule2.getStellar().getId()), Long.class))
                .andExpect(jsonPath("$.content[1].stellarNameKor", is(savedSchedule2.getStellar().getNameKor())))
                .andExpect(jsonPath("$.content[1].isFixedTime", is(savedSchedule2.getIsFixedTime())))
                .andExpect(jsonPath("$.content[1].startDateTime", is(
                        savedSchedule2.getStartDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))))
                .andExpect(jsonPath("$.content[1].title", is(savedSchedule2.getTitle())))
                .andExpect(jsonPath("$.content[1].remark", is(savedSchedule2.getRemark())));
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
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build());

        Stellar stellar2 = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름2")
                .nameEng("english name2")
                .nameJpn("日本語の名前2")
                .generation((byte)2)
                .debutOrder((byte)2)
                .personalColor("ffddaa")
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
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule.getId()), Long.class));

        // stellarIds
        String stellarIds = Stream.of(stellar.getId(), stellar2.getId())
                .map(String::valueOf).collect(Collectors.joining(","));
        mvc.perform(get(url)
                        .param("stellarId", stellarIds))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule.getId()), Long.class))
                .andExpect(jsonPath("$.content[1].id", is(savedSchedule2.getId()), Long.class));

        // date after
        mvc.perform(get(url)
                        .param("startDateTimeAfter", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule2.getId()), Long.class));

        // date before
        mvc.perform(get(url)
                        .param("startDateTimeBefore", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule.getId()), Long.class));

        // all filters
        mvc.perform(get(url)
                        .param("stellarId", String.valueOf(stellar.getId()))
                        .param("startDateTimeAfter", "2023-02-14T00:00:00")
                        .param("startDateTimeBefore", "2023-02-15T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id", is(savedSchedule.getId()), Long.class));
    }

    /**
     * Save a stellar used by the tests about the expected start window.
     * @return saved stellar
     */
    private Stellar saveStellar() {
        return stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build());
    }

    /**
     * Test for saving and getting a schedule with an expected start window ending on the next day.
     * The schedule history keeps the window too.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void saveScheduleWithStartWindow() throws Exception {
        //given
        Stellar stellar = saveStellar();
        LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 23, 30);
        LocalDateTime startDateTimeUntil = LocalDateTime.of(2023, 2, 15, 0, 30);

        ScheduleSaveRequestDto requestDto = ScheduleSaveRequestDto.builder()
                .stellarId(stellar.getId())
                .isFixedTime(true)
                .startDateTime(startDateTime)
                .startDateTimeUntil(startDateTimeUntil)
                .title("생일 방송 예정")
                .build();

        String url = "http://localhost:" + port + "/api/v1/schedules";

        //when
        String savedId = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        Schedule savedSchedule = scheduleRepository.findById(Long.valueOf(savedId)).orElseThrow();
        assertThat(savedSchedule.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(savedSchedule.getStartDateTimeUntil()).isEqualTo(startDateTimeUntil);

        mvc.perform(get(url + "/" + savedSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDateTime", is("2023-02-14T23:30:00")))
                .andExpect(jsonPath("$.startDateTimeUntil", is("2023-02-15T00:30:00")));

        mvc.perform(get("http://localhost:" + port + "/api/v1/schedule-histories")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.scheduleId == " + savedSchedule.getId() + ")].startDateTimeUntil",
                        contains("2023-02-15T00:30:00")));
    }

    /**
     * Test that updating without an expected start window clears it.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void updateScheduleClearsStartWindow() throws Exception {
        //given
        Stellar stellar = saveStellar();
        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 16, 0))
                .startDateTimeUntil(LocalDateTime.of(2023, 2, 14, 17, 0))
                .title("생일 방송 예정")
                .build());

        LocalDateTime actualStartDateTime = LocalDateTime.of(2023, 2, 14, 16, 23);
        ScheduleUpdateRequestDto requestDto = ScheduleUpdateRequestDto.builder()
                .isFixedTime(true)
                .startDateTime(actualStartDateTime)
                .title("생일 방송")
                .build();

        String url = "http://localhost:" + port + "/api/v1/schedules/" + savedSchedule.getId();

        //when
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        Schedule modifiedSchedule = scheduleRepository.findById(savedSchedule.getId()).orElseThrow();
        assertThat(modifiedSchedule.getStartDateTime()).isEqualTo(actualStartDateTime);
        assertThat(modifiedSchedule.getStartDateTimeUntil()).isNull();
    }

    /**
     * Test that an invalid expected start window is rejected on both saving and updating,
     * and that a window of exactly 12 hours is allowed.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void invalidStartWindow() throws Exception {
        //given
        Stellar stellar = saveStellar();
        LocalDateTime start = LocalDateTime.of(2023, 2, 14, 16, 0);
        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(start)
                .title("스케줄의 이름")
                .build());

        String saveUrl = "http://localhost:" + port + "/api/v1/schedules";
        String updateUrl = saveUrl + "/" + savedSchedule.getId();
        long countBefore = scheduleRepository.count();

        // (isFixedTime, startDateTimeUntil) pairs that must be rejected
        Object[][] invalidCases = {
                {false, start.plusHours(1)},                // time is not fixed
                {true, start},                              // same as the start
                {true, start.minusMinutes(30)},             // earlier than the start
                {true, start.plusHours(12).plusMinutes(1)}, // longer than 12 hours
        };

        for (Object[] invalidCase : invalidCases) {
            Boolean isFixedTime = (Boolean) invalidCase[0];
            LocalDateTime until = (LocalDateTime) invalidCase[1];

            //when, then
            mvc.perform(post(saveUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(ScheduleSaveRequestDto.builder()
                                    .stellarId(stellar.getId())
                                    .isFixedTime(isFixedTime)
                                    .startDateTime(start)
                                    .startDateTimeUntil(until)
                                    .title("스케줄의 이름")
                                    .build())))
                    .andExpect(status().isUnprocessableContent());

            mvc.perform(put(updateUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                    .isFixedTime(isFixedTime)
                                    .startDateTime(start)
                                    .startDateTimeUntil(until)
                                    .title("수정된 스케줄의 이름")
                                    .build())))
                    .andExpect(status().isUnprocessableContent());
        }

        // nothing was saved or changed
        assertThat(scheduleRepository.count()).isEqualTo(countBefore);
        Schedule unchangedSchedule = scheduleRepository.findById(savedSchedule.getId()).orElseThrow();
        assertThat(unchangedSchedule.getTitle()).isEqualTo("스케줄의 이름");
        assertThat(unchangedSchedule.getStartDateTimeUntil()).isNull();

        // exactly 12 hours is allowed
        mvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(true)
                                .startDateTime(start)
                                .startDateTimeUntil(start.plusHours(12))
                                .title("스케줄의 이름")
                                .build())))
                .andExpect(status().isOk());
        assertThat(scheduleRepository.findById(savedSchedule.getId()).orElseThrow().getStartDateTimeUntil())
                .isEqualTo(start.plusHours(12));
    }

    /**
     * Test for saving, getting and updating a day-off schedule.
     * The flag is returned by the schedule and schedule history APIs, and an omitted flag keeps the current value
     * unless the time is fixed (so clients unaware of the flag can still turn a day off into a stream).
     */
    @Test
    @WithMockUser(roles = "USER")
    public void saveAndUpdateDayOffSchedule() throws Exception {
        //given
        Stellar stellar = saveStellar();
        String url = "http://localhost:" + port + "/api/v1/schedules";

        ScheduleSaveRequestDto requestDto = ScheduleSaveRequestDto.builder()
                .stellarId(stellar.getId())
                .isFixedTime(false)
                .isDayOff(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 0, 0))
                .title("휴방")
                .build();

        //when
        String savedId = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThat(scheduleRepository.findById(Long.valueOf(savedId)).orElseThrow().getIsDayOff()).isTrue();
        mvc.perform(get(url + "/" + savedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDayOff", is(true)));
        mvc.perform(get("http://localhost:" + port + "/api/v1/schedule-histories")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.scheduleId == " + savedId + ")].isDayOff", contains(true)));

        //when: updated without the flag (e.g. by a client unaware of it)
        mvc.perform(put(url + "/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(false)
                                .startDateTime(LocalDateTime.of(2023, 2, 14, 0, 0))
                                .title("휴방 (감기)")
                                .build())))
                .andExpect(status().isOk());

        //then: the flag is kept
        Schedule keptSchedule = scheduleRepository.findById(Long.valueOf(savedId)).orElseThrow();
        assertThat(keptSchedule.getIsDayOff()).isTrue();
        assertThat(keptSchedule.getTitle()).isEqualTo("휴방 (감기)");

        //when: updated to a stream without the flag
        mvc.perform(put(url + "/" + savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(true)
                                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                                .title("방송")
                                .build())))
                .andExpect(status().isOk());

        //then
        assertThat(scheduleRepository.findById(Long.valueOf(savedId)).orElseThrow().getIsDayOff()).isFalse();
        mvc.perform(get(url + "/" + savedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDayOff", is(false)));
    }

    /**
     * Test that a day off with a fixed time is rejected on both saving and updating.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void invalidDayOffWithFixedTime() throws Exception {
        //given
        Stellar stellar = saveStellar();
        LocalDateTime start = LocalDateTime.of(2023, 2, 14, 19, 0);
        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(start)
                .title("방송")
                .build());
        String url = "http://localhost:" + port + "/api/v1/schedules";
        long countBefore = scheduleRepository.count();

        //when, then
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleSaveRequestDto.builder()
                                .stellarId(stellar.getId())
                                .isFixedTime(true)
                                .isDayOff(true)
                                .startDateTime(start)
                                .title("휴방")
                                .build())))
                .andExpect(status().isUnprocessableContent());

        mvc.perform(put(url + "/" + savedSchedule.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(true)
                                .isDayOff(true)
                                .startDateTime(start)
                                .title("휴방")
                                .build())))
                .andExpect(status().isUnprocessableContent());

        assertThat(scheduleRepository.count()).isEqualTo(countBefore);
        Schedule unchangedSchedule = scheduleRepository.findById(savedSchedule.getId()).orElseThrow();
        assertThat(unchangedSchedule.getIsDayOff()).isFalse();
        assertThat(unchangedSchedule.getTitle()).isEqualTo("방송");
    }

    /**
     * Test that a stellar cannot have more than one day off (not deleted) on a date,
     * on both saving and updating.
     */
    @Test
    @WithMockUser(roles = "USER")
    public void duplicateDayOff() throws Exception {
        //given
        Stellar stellar = saveStellar();
        Stellar otherStellar = saveStellar();
        LocalDateTime day = LocalDateTime.of(2023, 2, 14, 0, 0);
        Schedule dayOff = scheduleRepository.save(Schedule.builder()
                .stellar(stellar).isFixedTime(false).isDayOff(true).startDateTime(day).title("휴방").build());
        String url = "http://localhost:" + port + "/api/v1/schedules";
        long countBefore = scheduleRepository.count();

        //when, then: same stellar and date (the time is truncated for a day off)
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleSaveRequestDto.builder()
                                .stellarId(stellar.getId()).isFixedTime(false).isDayOff(true)
                                .startDateTime(day.plusHours(19)).title("휴방*").build())))
                .andExpect(status().isUnprocessableContent());
        assertThat(scheduleRepository.count()).isEqualTo(countBefore);

        // another stellar, the next day, or a schedule that is not a day off is allowed
        for (ScheduleSaveRequestDto allowed : new ScheduleSaveRequestDto[] {
                ScheduleSaveRequestDto.builder().stellarId(otherStellar.getId()).isFixedTime(false).isDayOff(true)
                        .startDateTime(day).title("휴방").build(),
                ScheduleSaveRequestDto.builder().stellarId(stellar.getId()).isFixedTime(false).isDayOff(true)
                        .startDateTime(day.plusDays(1)).title("휴방").build(),
                ScheduleSaveRequestDto.builder().stellarId(stellar.getId()).isFixedTime(true)
                        .startDateTime(day.plusHours(21)).title("스페이스").build(),
        }) {
            mvc.perform(post(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(allowed)))
                    .andExpect(status().isOk());
        }

        // updating the day off itself (without the flag) is allowed
        mvc.perform(put(url + "/" + dayOff.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(false).startDateTime(day).title("휴방 (감기)").build())))
                .andExpect(status().isOk());

        // moving the next day's day off onto the date is rejected, and nothing changes
        Schedule nextDayOff = scheduleRepository.findAll().stream()
                .filter(s -> s.getStellar().getId().equals(stellar.getId())
                        && s.getStartDateTime().equals(day.plusDays(1)))
                .findFirst().orElseThrow();
        int historyCountBefore = scheduleHistoryRepository.findAll().size();
        mvc.perform(put(url + "/" + nextDayOff.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleUpdateRequestDto.builder()
                                .isFixedTime(false).startDateTime(day).title("휴방").build())))
                .andExpect(status().isUnprocessableContent());
        assertThat(scheduleRepository.findById(nextDayOff.getId()).orElseThrow().getStartDateTime())
                .isEqualTo(day.plusDays(1));
        assertThat(scheduleHistoryRepository.findAll().size()).isEqualTo(historyCountBefore);

        // after the day off is deleted, a new one can be saved
        mvc.perform(delete(url + "/" + dayOff.getId()))
                .andExpect(status().isOk());
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(ScheduleSaveRequestDto.builder()
                                .stellarId(stellar.getId()).isFixedTime(false).isDayOff(true)
                                .startDateTime(day).title("휴방").build())))
                .andExpect(status().isOk());
    }
}
