package com.footballtalks.footballtalks.scheduler;

import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.service.ApiFootballPlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Component
public class PlayerSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(PlayerSyncScheduler.class);

    // Top leagues to sync — add or remove league IDs as needed
    private static final List<Integer> LEAGUES_TO_SYNC = Arrays.asList(
            39,   // Premier League (England)
            140,  // La Liga (Spain)
            135,  // Serie A (Italy)
            78,   // Bundesliga (Germany)
            61,   // Ligue 1 (France)
            2,    // Champions League
            3,    // Europa League
            88,   // Eredivisie (Netherlands)
            94,   // Primeira Liga (Portugal)
            203   // Super Lig (Turkey)
    );

    private final ApiFootballPlayerService apiFootballPlayerService;
    private final int season;
    private final LocalDate syncStartDate;
    private final Clock clock;

    public PlayerSyncScheduler(ApiFootballPlayerService apiFootballPlayerService,
                               @Value("${api.football.default-season:2024}") int season,
                               @Value("${api.football.sync-start-date:2024-01-01}") String syncStartDate) {
        this(apiFootballPlayerService, season, LocalDate.parse(syncStartDate), Clock.systemUTC());
    }

    PlayerSyncScheduler(ApiFootballPlayerService apiFootballPlayerService,
                        int season,
                        LocalDate syncStartDate,
                        Clock clock) {
        this.apiFootballPlayerService = apiFootballPlayerService;
        this.season = season;
        this.syncStartDate = syncStartDate;
        this.clock = clock;
    }

    /**
     * Runs once every day at 3:00 AM UTC.
     * Syncs ONE league per day to stay within the free API limit (100 requests/day).
     * Cycles through all leagues in LEAGUES_TO_SYNC over multiple days using the UTC date,
     * so the schedule stays consistent across application restarts.
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void syncNextLeague() {
        if (!apiFootballPlayerService.isConfigured()) {
            log.warn("API-Football key not configured — skipping player sync");
            return;
        }

        LocalDate todayUtc = LocalDate.now(clock);
        int index = resolveLeagueIndex(todayUtc);
        int leagueId = LEAGUES_TO_SYNC.get(index);

        log.info("Starting daily player sync for league {} on {} UTC (index {}/{})",
                leagueId, todayUtc, index + 1, LEAGUES_TO_SYNC.size());

        try {
            List<PlayerResponse> players = apiFootballPlayerService.getPlayers(
                    null,         // no search filter
                    null,         // no position filter
                    null,         // no team filter
                    null,         // no competition filter
                    null,         // default sort
                    null,         // default direction
                    null,         // no limit
                    leagueId,     // specific league
                    season,       // configured season
                    true          // force refresh from API
            );

            log.info("Successfully synced {} players for league {} (season {})", players.size(), leagueId, season);

        } catch (Exception e) {
            log.error("Failed to sync players for league {}: {}", leagueId, e.getMessage(), e);
        }
    }

    int resolveLeagueIndex(LocalDate syncDateUtc) {
        long daysSinceStart = ChronoUnit.DAYS.between(syncStartDate, syncDateUtc);
        return (int) Math.floorMod(daysSinceStart, LEAGUES_TO_SYNC.size());
    }
}
