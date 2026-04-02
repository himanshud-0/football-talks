package com.footballtalks.footballtalks.scheduler;

import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.service.ApiFootballPersistenceService;
import com.footballtalks.footballtalks.service.ApiFootballPlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    // Tracks which league index to sync next (cycles through the list)
    private final AtomicInteger currentLeagueIndex = new AtomicInteger(0);

    public PlayerSyncScheduler(ApiFootballPlayerService apiFootballPlayerService,
                               @Value("${api.football.default-season:2024}") int season) {
        this.apiFootballPlayerService = apiFootballPlayerService;
        this.season = season;
    }

    /**
     * Runs once every day at 3:00 AM UTC.
     * Syncs ONE league per day to stay within the free API limit (100 requests/day).
     * Cycles through all leagues in LEAGUES_TO_SYNC over multiple days.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void syncNextLeague() {
        if (!apiFootballPlayerService.isConfigured()) {
            log.warn("API-Football key not configured — skipping player sync");
            return;
        }

        int index = currentLeagueIndex.getAndUpdate(i -> (i + 1) % LEAGUES_TO_SYNC.size());
        int leagueId = LEAGUES_TO_SYNC.get(index);

        log.info("Starting daily player sync for league {} (index {}/{})", leagueId, index + 1, LEAGUES_TO_SYNC.size());

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
}