package com.footballtalks.footballtalks.scheduler;

import com.footballtalks.footballtalks.service.ApiFootballPlayerService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerSyncSchedulerTest {

    @Test
    void resolvesLeagueIndexFromUtcDate() {
        PlayerSyncScheduler scheduler = new PlayerSyncScheduler(
                null,
                2024,
                LocalDate.of(2024, 1, 1),
                Clock.systemUTC()
        );

        assertThat(scheduler.resolveLeagueIndex(LocalDate.of(2024, 1, 1))).isEqualTo(0);
        assertThat(scheduler.resolveLeagueIndex(LocalDate.of(2024, 1, 2))).isEqualTo(1);
        assertThat(scheduler.resolveLeagueIndex(LocalDate.of(2024, 1, 11))).isEqualTo(0);
    }

    @Test
    void restartOnSameUtcDayKeepsSameLeagueSelection() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-04-02T03:00:00Z"), ZoneOffset.UTC);

        PlayerSyncScheduler firstInstance = new PlayerSyncScheduler(
                null,
                2024,
                LocalDate.of(2024, 1, 1),
                fixedClock
        );
        PlayerSyncScheduler secondInstance = new PlayerSyncScheduler(
                null,
                2024,
                LocalDate.of(2024, 1, 1),
                fixedClock
        );

        LocalDate syncDate = LocalDate.now(fixedClock);
        assertThat(firstInstance.resolveLeagueIndex(syncDate))
                .isEqualTo(secondInstance.resolveLeagueIndex(syncDate));
    }
}
