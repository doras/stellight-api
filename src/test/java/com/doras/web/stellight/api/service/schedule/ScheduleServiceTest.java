package com.doras.web.stellight.api.service.schedule;

import com.doras.web.stellight.api.domain.schedule.Schedule;
import com.doras.web.stellight.api.domain.schedule.ScheduleHistory;
import com.doras.web.stellight.api.domain.schedule.ScheduleRepository;
import com.doras.web.stellight.api.domain.stellar.Stellar;
import com.doras.web.stellight.api.domain.stellar.StellarRepository;
import com.doras.web.stellight.api.exception.InvalidArgumentException;
import com.doras.web.stellight.api.exception.ScheduleNotFoundException;
import com.doras.web.stellight.api.web.dto.ScheduleFindAllRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleResponseDto;
import com.doras.web.stellight.api.web.dto.ScheduleSaveRequestDto;
import com.doras.web.stellight.api.web.dto.ScheduleUpdateRequestDto;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


/**
 * Test class for {@link ScheduleService}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StellarRepository stellarRepository;

    /**
     * Clean up all repositories after each test.
     */
    @AfterEach
    public void cleanup() {
        scheduleRepository.deleteAll();
        stellarRepository.deleteAll();
    }

    /**
     * Test saving of schedule and its history.
     */
    @Test
    public void testSave() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

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

        // when
        Long scheduleId = scheduleService.save(requestDto);

        // then
        // Because of lazy fetch about ScheduleHistory, must use additional transaction code.
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // schedule check
                Schedule savedSchedule = scheduleRepository.findAll().get(0);
                assertThat(savedSchedule.getId()).isEqualTo(scheduleId);
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
     * Test if a save fails when an invalid stellar is given
     */
    @Test
    public void testSaveWithInvalidStellar() {
        // given
        Long stellarId = 123L;
        ScheduleSaveRequestDto requestDto = ScheduleSaveRequestDto.builder()
                .stellarId(stellarId)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build();

        // when
        Throwable thrown = catchThrowable(() -> scheduleService.save(requestDto));

        // then
        assertThat(thrown).isInstanceOf(InvalidArgumentException.class)
                .hasMessage("존재하지 않는 스텔라 id 입니다. id = " + stellarId);
    }

    /**
     * Test finding a schedule by id.
     */
    @Test
    public void testFindById() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Boolean isFixedTime = true;
        LocalDateTime startDateTime = LocalDateTime.of(2023, 2, 14, 19, 0);
        String title = "스케줄의 이름";
        String remark = "스케줄에 대한 비고";

        Schedule schedule = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build());

        // when
        ScheduleResponseDto responseDto = scheduleService.findById(schedule.getId());

        // then
        assertThat(responseDto).isEqualTo(new ScheduleResponseDto(schedule));
    }

    /**
     * Test if an exception is thrown when a schedule is not found.
     */
    @Test
    public void testFindByIdNotFound() {
        // given
        Long id = 123L;

        // when
        Throwable throwable = catchThrowable(() -> scheduleService.findById(id));

        // then
        assertThat(throwable).isInstanceOf(ScheduleNotFoundException.class)
                .hasMessage(new ScheduleNotFoundException(id).getMessage());
    }

    /**
     * Test if the schedule is updated and the history of the schedule updated is saved.
     */
    @Test
    public void testUpdate() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("기존 스케줄 이름")
                .remark("기존 스케줄 비고")
                .build());

        Boolean expectedIsFixedTime = true;
        LocalDateTime expectedStartDateTime = LocalDateTime.of(2023, 2, 16, 21, 0);
        String expectedTitle = "스케줄의 이름";
        String expectedRemark = "스케줄에 대한 비고";

        Long scheduleId = savedSchedule.getId();

        ScheduleUpdateRequestDto requestDto = ScheduleUpdateRequestDto.builder()
                .isFixedTime(expectedIsFixedTime)
                .startDateTime(expectedStartDateTime)
                .title(expectedTitle)
                .remark(expectedRemark)
                .build();

        // when
        Long resultId = scheduleService.update(scheduleId, requestDto);

        // then
        assertThat(resultId).isEqualTo(scheduleId);

        // Because of lazy fetch about ScheduleHistory, must use additional transaction code.
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // schedule check
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
     * Test if an exception is thrown when the schedule to update is not found.
     */
    @Test
    public void testUpdateNotFound() {
        // given
        Long id = 123L;

        ScheduleUpdateRequestDto requestDto = ScheduleUpdateRequestDto.builder()
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 16, 21, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build();

        // when
        Throwable throwable = catchThrowable(() -> scheduleService.update(id, requestDto));

        // then
        assertThat(throwable).isInstanceOf(ScheduleNotFoundException.class)
                .hasMessage(new ScheduleNotFoundException(id).getMessage());
    }

    /**
     * Test deleting.
     */
    @Test
    public void testDelete() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule schedule = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄의 이름")
                .remark("스케줄에 대한 비고")
                .build());

        // when
        scheduleService.delete(schedule.getId());

        // then
        Schedule newSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        assertThat(newSchedule.getIsDeleted()).isEqualTo(true);
    }

    /**
     * Test if an exception is thrown when the schedule to delete is not found.
     */
    @Test
    public void testDeleteNotFound() {
        // given
        Long id = 123L;

        // when
        Throwable throwable = catchThrowable(() -> scheduleService.delete(id));

        // then
        assertThat(throwable).isInstanceOf(ScheduleNotFoundException.class)
                .hasMessage(new ScheduleNotFoundException(id).getMessage());
    }

    /**
     * Test finding all schedules without any filters.
     */
    @Test
    public void testFindAllSchedules() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄 이름")
                .remark("스케줄 비고")
                .build());

        Schedule savedSchedule2 = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 20, 19, 0))
                .title("스케줄 이름 2")
                .remark("스케줄 비고 2")
                .build());

        ScheduleFindAllRequestDto requestDto = new ScheduleFindAllRequestDto();

        // when
        List<ScheduleResponseDto> result = scheduleService.findAllSchedules(requestDto);

        // then
        List<ScheduleResponseDto> expectedResult =
                Stream.of(savedSchedule, savedSchedule2).map(ScheduleResponseDto::new).collect(Collectors.toList());
        assertThat(result)
                .hasSize(expectedResult.size())
                .containsExactlyElementsOf(expectedResult);
    }

    /**
     * Test finding all schedules with filters.
     */
    @Test
    public void testFindAllSchedulesWithFilters() {
        // given
        Stellar savedStellar = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름")
                .nameEng("english name")
                .nameJpn("日本語の名前")
                .build());

        Stellar savedStellar2 = stellarRepository.save(Stellar.builder()
                .nameKor("한국 이름2")
                .nameEng("english name2")
                .nameJpn("日本語の名前2")
                .build());

        Schedule savedSchedule = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 14, 19, 0))
                .title("스케줄 이름")
                .remark("스케줄 비고")
                .build());

        Schedule savedSchedule2 = scheduleRepository.save(Schedule.builder()
                .stellar(savedStellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023, 2, 20, 19, 0))
                .title("스케줄 이름 2")
                .remark("스케줄 비고 2")
                .build());

        ScheduleFindAllRequestDto noMatchStellarIdRequestDto = ScheduleFindAllRequestDto.builder()
                .stellarId(new Long[] { savedStellar2.getId() })
                .build();

        ScheduleFindAllRequestDto allStellarIdRequestDto = ScheduleFindAllRequestDto.builder()
                .stellarId(new Long[] {savedStellar.getId(), savedStellar2.getId()})
                .build();

        ScheduleFindAllRequestDto afterRequestDto = ScheduleFindAllRequestDto.builder()
                .startDateTimeAfter(LocalDateTime.of(2023, 2, 15, 19, 0))
                .build();

        ScheduleFindAllRequestDto beforeRequestDto = ScheduleFindAllRequestDto.builder()
                .startDateTimeBefore(LocalDateTime.of(2023, 2, 15, 19, 0))
                .build();

        ScheduleFindAllRequestDto afterAndBeforeRequestDto = ScheduleFindAllRequestDto.builder()
                .startDateTimeAfter(LocalDateTime.of(2023, 2, 15, 19, 0))
                .startDateTimeBefore(LocalDateTime.of(2023, 2, 21, 19, 0))
                .build();

        // when
        List<ScheduleResponseDto> noMatchStellarIdResult = scheduleService.findAllSchedules(noMatchStellarIdRequestDto);
        List<ScheduleResponseDto> allStellarIdResult = scheduleService.findAllSchedules(allStellarIdRequestDto);
        List<ScheduleResponseDto> afterResult = scheduleService.findAllSchedules(afterRequestDto);
        List<ScheduleResponseDto> beforeResult = scheduleService.findAllSchedules(beforeRequestDto);
        List<ScheduleResponseDto> afterAndBeforeResult = scheduleService.findAllSchedules(afterAndBeforeRequestDto);

        // then
        assertThat(noMatchStellarIdResult).hasSize(0);

        assertThat(Stream.of(savedSchedule, savedSchedule2).map(ScheduleResponseDto::new).collect(Collectors.toList()))
                .hasSize(allStellarIdResult.size())
                .containsExactlyElementsOf(allStellarIdResult);

        assertThat(Stream.of(savedSchedule2).map(ScheduleResponseDto::new).collect(Collectors.toList()))
                .hasSize(afterResult.size())
                .containsExactlyElementsOf(afterResult);

        assertThat(Stream.of(savedSchedule).map(ScheduleResponseDto::new).collect(Collectors.toList()))
                .hasSize(beforeResult.size())
                .containsExactlyElementsOf(beforeResult);

        assertThat(Stream.of(savedSchedule2).map(ScheduleResponseDto::new).collect(Collectors.toList()))
                .hasSize(afterAndBeforeResult.size())
                .containsExactlyElementsOf(afterAndBeforeResult);
    }
}
