package com.footballtalks.footballtalks.scheduler;

import com.footballtalks.footballtalks.dto.CompetitionSummaryResponse;
import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.dto.TeamSummaryResponse;
import com.footballtalks.footballtalks.service.ApiFootballPersistenceService;
import com.footballtalks.footballtalks.service.ApiFootballPlayerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PlayerSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(PlayerSyncScheduler.class);

    private static final List<Integer> LEAGUES_TO_SYNC = Arrays.asList(
            39,   // Premier League
            140,  // La Liga
            135,  // Serie A
            78,   // Bundesliga
            61,   // Ligue 1
            2,    // Champions League
            88,   // Eredivisie
            94,   // Primeira Liga
            203,  // Super Lig
            3     // Europa League
    );

    private final ApiFootballPlayerService apiFootballPlayerService;
    private final ApiFootballPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final int season;
    private final HttpClient httpClient;
    private int currentIndex = 0;

    public PlayerSyncScheduler(ApiFootballPlayerService apiFootballPlayerService,
                                ApiFootballPersistenceService persistenceService,
                                ObjectMapper objectMapper,
                                @Value("${api.football.key:}") String apiKey,
                                @Value("${api.football.default-season:2024}") int season) {
        this.apiFootballPlayerService = apiFootballPlayerService;
        this.persistenceService = persistenceService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.season = season;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void syncNextLeague() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("API_FOOTBALL_KEY not configured — skipping sync");
            return;
        }

        int leagueId = LEAGUES_TO_SYNC.get(currentIndex);
        currentIndex = (currentIndex + 1) % LEAGUES_TO_SYNC.size();

        log.info("Starting daily sync for league {} season {}", leagueId, season);

        try {
            int totalFetched = fetchAndPersistLeague(leagueId, season);
            log.info("Synced {} players for league {}", totalFetched, leagueId);
        } catch (Exception e) {
            log.error("Failed to sync league {}: {}", leagueId, e.getMessage(), e);
        }
    }

    private int fetchAndPersistLeague(int leagueId, int season) throws Exception {
        int page = 1;
        int totalPages = 1;
        int totalFetched = 0;

        while (page <= totalPages && page <= 5) {
            String url = String.format(
                    "https://v3.football.api-sports.io/players?league=%d&season=%d&page=%d",
                    leagueId, season, page
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-apisports-key", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("API returned status {} for league {}", response.statusCode(), leagueId);
                break;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode errors = root.path("errors");
            if (!errors.isMissingNode() && errors.size() > 0) {
                log.warn("API errors for league {}: {}", leagueId, errors);
                break;
            }

            totalPages = root.path("paging").path("total").asInt(1);
            JsonNode responseArray = root.path("response");
            if (!responseArray.isArray() || responseArray.size() == 0) break;

            List<PlayerResponse> players = new ArrayList<>();
            for (JsonNode item : responseArray) {
                PlayerResponse player = parsePlayer(item);
                if (player != null) players.add(player);
            }

            int persisted = persistenceService.persistPlayers(players, String.valueOf(season));
            totalFetched += persisted;
            log.info("League {} page {}/{}: persisted {} players", leagueId, page, totalPages, persisted);

            page++;
            if (page <= totalPages) Thread.sleep(300);
        }

        return totalFetched;
    }

    private PlayerResponse parsePlayer(JsonNode item) {
        try {
            JsonNode p = item.path("player");
            JsonNode stats = item.path("statistics");
            JsonNode stat = (stats.isArray() && stats.size() > 0) ? stats.get(0) : objectMapper.createObjectNode();
            JsonNode teamNode = stat.path("team");
            JsonNode leagueNode = stat.path("league");
            JsonNode gamesNode = stat.path("games");
            JsonNode goalsNode = stat.path("goals");
            JsonNode cardsNode = stat.path("cards");

            TeamSummaryResponse team = null;
            if (teamNode.has("id")) {
                team = new TeamSummaryResponse(
                        teamNode.path("id").asLong(),
                        teamNode.path("name").asText(null),
                        leagueNode.path("country").asText(null),
                        leagueNode.path("name").asText(null),
                        teamNode.path("logo").asText(null)
                );
            }

            CompetitionSummaryResponse competition = null;
            if (leagueNode.has("id")) {
                competition = new CompetitionSummaryResponse(
                        leagueNode.path("id").asLong(),
                        leagueNode.path("name").asText(null),
                        leagueNode.path("country").asText(null),
                        leagueNode.path("logo").asText(null)
                );
            }

            PlayerResponse player = new PlayerResponse();
            player.setId(p.path("id").asLong());
            player.setName(p.path("name").asText(null));
            player.setFirstname(p.path("firstname").asText(null));
            player.setLastname(p.path("lastname").asText(null));
            player.setAge(p.has("age") ? p.path("age").asInt() : null);
            player.setNationality(p.path("nationality").asText(null));
            player.setPosition(gamesNode.path("position").asText(null));
            player.setPhoto(p.path("photo").asText(null));
            player.setImage(p.path("photo").asText(null));
            player.setTeamSummary(team);
            player.setCompetition(competition);
            player.setTeam(team != null ? team.getName() : null);
            player.setClub(team != null ? team.getName() : null);
            player.setLeague(competition != null ? competition.getName() : null);
            player.setMarketValue(0L);
            player.setAppearances(gamesNode.path("appearences").asInt(0));
            player.setGoalsScored(goalsNode.path("total").asInt(0));
            player.setAssists(goalsNode.path("assists").asInt(0));
            player.setMinutesPlayed(gamesNode.path("minutes").asInt(0));
            player.setYellowCards(cardsNode.path("yellow").asInt(0));
            player.setRedCards(cardsNode.path("red").asInt(0));
            try {
                player.setRating(new BigDecimal(gamesNode.path("rating").asText("0")));
            } catch (Exception e) {
                player.setRating(BigDecimal.ZERO);
            }
            player.setSpecialAbility("");
            player.setDescription(player.getName() + " is a " +
                    (player.getPosition() != null ? player.getPosition().toLowerCase() : "footballer") +
                    (team != null ? " playing for " + team.getName() : "") + ".");
            return player;
        } catch (Exception e) {
            log.warn("Failed to parse player: {}", e.getMessage());
            return null;
        }
    }
}
