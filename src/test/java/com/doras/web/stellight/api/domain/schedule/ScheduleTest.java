package com.doras.web.stellight.api.domain.schedule;

import com.doras.web.stellight.api.domain.stellar.Stellar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link Schedule}.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScheduleTest {

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Clean up entityManager after each test.
     */
    @AfterEach
    public void cleanup() {
        entityManager.clear();
    }

    /**
     * Test for persisting the entity.
     */
    @Test
    public void persist() {

        // case 1. isFixedTime == true

        //given
        Stellar stellar = Stellar.builder()
                .nameKor("스텔라")
                .nameEng("")
                .nameJpn("")
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build();
        entityManager.persist(stellar);

        Boolean isFixedTime = true;
        LocalDateTime startDateTime = LocalDateTime.of(2023,2,14,19,0);
        String title = "제목";
        String remark = "비고";

        //when
        Schedule entity = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(isFixedTime)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build();
        entityManager.persist(entity);
        entityManager.flush();

        //then
        Schedule foundEntity = entityManager.find(Schedule.class, entity.getId());
        assertThat(foundEntity.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(foundEntity.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(foundEntity.getTitle()).isEqualTo(title);
        assertThat(foundEntity.getRemark()).isEqualTo(remark);

        Collection<ScheduleHistory> histories = foundEntity.getScheduleHistories();
        assertThat(histories.size()).isEqualTo(1);
        ScheduleHistory scheduleHistory = histories.iterator().next();
        assertThat(scheduleHistory.getSchedule().getId()).isEqualTo(foundEntity.getId());
        assertThat(scheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(scheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(scheduleHistory.getTitle()).isEqualTo(title);
        assertThat(scheduleHistory.getRemark()).isEqualTo(remark);

        // case 2. isFixedTime == false

        //given
        startDateTime = LocalDateTime.of(2023,2,14,19,0);
        LocalDateTime expectedStartDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);

        //when
        entity = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(startDateTime)
                .title(title)
                .remark(remark)
                .build();
        entityManager.persist(entity);
        entityManager.flush();

        //then
        foundEntity = entityManager.find(Schedule.class, entity.getId());
        assertThat(foundEntity.getIsFixedTime()).isEqualTo(false);
        assertThat(foundEntity.getStartDateTime()).isEqualTo(expectedStartDateTime);
        assertThat(foundEntity.getTitle()).isEqualTo(title);
        assertThat(foundEntity.getRemark()).isEqualTo(remark);

        histories = foundEntity.getScheduleHistories();
        assertThat(histories.size()).isEqualTo(1);
        scheduleHistory = histories.iterator().next();
        assertThat(scheduleHistory.getSchedule().getId()).isEqualTo(foundEntity.getId());
        assertThat(scheduleHistory.getIsFixedTime()).isEqualTo(false);
        assertThat(scheduleHistory.getStartDateTime()).isEqualTo(expectedStartDateTime);
        assertThat(scheduleHistory.getTitle()).isEqualTo(title);
        assertThat(scheduleHistory.getRemark()).isEqualTo(remark);
    }

    /**
     * Test for updating the entity.
     */
    @Test
    public void update() {
        // case 1. isFixedTime == true

        //given
        Stellar stellar = Stellar.builder()
                .nameKor("스텔라")
                .nameEng("")
                .nameJpn("")
                .generation((byte)2)
                .debutOrder((byte)1)
                .personalColor("ffddaa")
                .build();
        entityManager.persist(stellar);

        Schedule entity = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(false)
                .startDateTime(LocalDateTime.of(2023,2,1,1,0))
                .title("제목")
                .remark("비고")
                .build();
        entityManager.persist(entity);

        //when
        Boolean isFixedTime = true;
        LocalDateTime startDateTime = LocalDateTime.of(2023,2,14,19,0);
        String title = "수정 제목";
        String remark = "수정 비고";

        entity.update(isFixedTime, startDateTime, title, remark);

        entityManager.persist(entity);

        //then
        assertThat(entity.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(entity.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(entity.getTitle()).isEqualTo(title);
        assertThat(entity.getRemark()).isEqualTo(remark);

        Collection<ScheduleHistory> histories = entity.getScheduleHistories();
        assertThat(histories.size()).isEqualTo(2);
        ScheduleHistory scheduleHistory = histories.stream()
                .max(Comparator.comparing(ScheduleHistory::getId)).orElseThrow();
        assertThat(scheduleHistory.getSchedule().getId()).isEqualTo(entity.getId());
        assertThat(scheduleHistory.getIsFixedTime()).isEqualTo(isFixedTime);
        assertThat(scheduleHistory.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(scheduleHistory.getTitle()).isEqualTo(title);
        assertThat(scheduleHistory.getRemark()).isEqualTo(remark);

        // case 2. isFixedTime == false

        //given
        entity = Schedule.builder()
                .stellar(stellar)
                .isFixedTime(true)
                .startDateTime(LocalDateTime.of(2023,2,1,1,0))
                .title("제목")
                .remark("비고")
                .build();

        entityManager.persist(entity);

        startDateTime = LocalDateTime.of(2023,2,14,19,0);
        LocalDateTime expectedStartDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS);

        //when
        entity.update(false, startDateTime, title, remark);

        entityManager.persist(entity);

        //then
        assertThat(entity.getIsFixedTime()).isEqualTo(false);
        assertThat(entity.getStartDateTime()).isEqualTo(expectedStartDateTime);
        assertThat(entity.getTitle()).isEqualTo(title);
        assertThat(entity.getRemark()).isEqualTo(remark);

        histories = entity.getScheduleHistories();
        assertThat(histories.size()).isEqualTo(2);
        scheduleHistory = histories.stream()
                .max(Comparator.comparing(ScheduleHistory::getId)).orElseThrow();
        assertThat(scheduleHistory.getSchedule().getId()).isEqualTo(entity.getId());
        assertThat(scheduleHistory.getIsFixedTime()).isEqualTo(false);
        assertThat(scheduleHistory.getStartDateTime()).isEqualTo(expectedStartDateTime);
        assertThat(scheduleHistory.getTitle()).isEqualTo(title);
        assertThat(scheduleHistory.getRemark()).isEqualTo(remark);
    }
}
