package com.footballtalks.footballtalks.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballtalks.footballtalks.dto.CompetitionSummaryResponse;
import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.dto.TeamSummaryResponse;
import com.footballtalks.footballtalks.service.ApiFootballPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Syncs one league per day using api-football.com.
 * Fetches full player data: photos, stats, market values.
 * Cycles through all configured leagues automatically.
 */
@Component
public class PlayerSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(PlayerSyncScheduler.class);

    // All major leagues with api-football.com IDs
    // Add more IDs here to sync more leagues
    private static final List<Integer> ALL_LEAGUES = List.of(
            39,   // Premier League (England)
            140,  // La Liga (Spain)
            135,  // Serie A (Italy)
            78,   // Bundesliga (Germany)
            61,   // Ligue 1 (France)
            307,  // Saudi Pro League
            2,    // Champions League
            3,    // Europa League
            88,   // Eredivisie (Netherlands)
            94,   // Primeira Liga (Portugal)
            203,  // Super Lig (Turkey)
            253,  // MLS (USA)
            71,   // Brasileirao (Brazil)
            283   // Indian Super League
    );

    private final ApiFootballPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;
    private final int defaultSeason;
    private final HttpClient httpClient;

    // Tracks which league to sync next
    private int currentIndex = 0;

    public PlayerSyncScheduler(ApiFootballPersistenceService persistenceService,
                               ObjectMapper objectMapper,
                               @Value("${api.football.key:}") String apiKey,
                               @Value("${api.football.base-url:https://v3.football.api-sports.io}") String baseUrl,
                               @Value("${api.football.default-season:2024}") int defaultSeason) {
        this.persistenceService = persistenceService;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.defaultSeason = defaultSeason;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Runs every day at 3 AM UTC — syncs one league per run.
     * With 14 leagues, full sync completes in 14 days.
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void syncNextLeague() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("API_FOOTBALL_KEY not configured — skipping sync");
            return;
        }

        int leagueId = ALL_LEAGUES.get(currentIndex);
        currentIndex = (currentIndex + 1) % ALL_LEAGUES.size();

        log.info("Starting daily sync for league {} (season {})", leagueId, defaultSeason);
        try {
            int total = syncLeague(leagueId, defaultSeason);
            log.info("Synced {} players for league {}", total, leagueId);
        } catch (Exception e) {
            log.error("Failed to sync league {}: {}", leagueId, e.getMessage(), e);
        }
    }

    /**
     * Manual trigger — call via POST /api/admin/sync
     * Syncs the next league in the list immediately.
     */
    public void triggerSync() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("API_FOOTBALL_KEY not configured — skipping manual sync");
            return;
        }

        int leagueId = ALL_LEAGUES.get(currentIndex);
        currentIndex = (currentIndex + 1) % ALL_LEAGUES.size();

        log.info("Manual sync triggered for league {} (season {})", leagueId, defaultSeason);
        try {
            int total = syncLeague(leagueId, defaultSeason);
            log.info("Manual sync complete: {} players for league {}", total, leagueId);
        } catch (Exception e) {
            log.error("Manual sync failed for league {}: {}", leagueId, e.getMessage(), e);
        }
    }

    /**
     * Syncs all players for a given league and season.
     * Fetches page by page until all players are retrieved.
     * Each page has 20 players. Adds 300ms delay between pages
     * to respect API rate limits.
     */
    private int syncLeague(int leagueId, int season) throws Exception {
        LinkedHashMap<Long, PlayerResponse> playerMap = new LinkedHashMap<>();
        int page = 1;

        while (true) {
            JsonNode root = get("/players", Map.of(
                    "league", String.valueOf(leagueId),
                    "season", String.valueOf(season),
                    "page", String.valueOf(page)
            ));

            JsonNode response = root.path("response");
            JsonNode paging = root.path("paging");

            if (!response.isArray() || response.size() == 0) {
                log.info("League {} page {}: no more players", leagueId, page);
                break;
            }

            int totalPages = paging.path("total").asInt(1);
            log.info("League {} page {}/{}: {} players", leagueId, page, totalPages, response.size());

            for (JsonNode item : response) {
                PlayerResponse player = parsePlayer(item, leagueId);
                if (player != null && player.getId() != null) {
                    playerMap.merge(player.getId(), player, this::mergePlayer);
                }
            }

            if (page >= totalPages || page >= 3) break;
            page++;

            // Respect rate limit: 30 requests/minute on free tier
            Thread.sleep(300);
        }

        if (playerMap.isEmpty()) return 0;

        List<PlayerResponse> players = new ArrayList<>(playerMap.values());
        int persisted = persistenceService.persistPlayers(players, String.valueOf(season));
        log.info("League {}: persisted {} players to DB", leagueId, persisted);
        return persisted;
    }

    /**
     * Parses a single player from the api-football response.
     * Extracts: photo, stats, goals, assists, rating, cards, position, nationality, age.
     * Market value is not available on free tier — set to 0.
     */
    private PlayerResponse parsePlayer(JsonNode item, int leagueId) {
        try {
            JsonNode playerNode = item.path("player");
            JsonNode statsArray = item.path("statistics");
            JsonNode statsNode = statsArray.isArray() && statsArray.size() > 0
                    ? statsArray.get(0)
                    : objectMapper.createObjectNode();

            JsonNode teamNode = statsNode.path("team");
            JsonNode leagueNode = statsNode.path("league");
            JsonNode gamesNode = statsNode.path("games");
            JsonNode goalsNode = statsNode.path("goals");
            JsonNode cardsNode = statsNode.path("cards");
            JsonNode passesNode = statsNode.path("passes");

            Long playerId = longOrNull(playerNode, "id");
            if (playerId == null) return null;

            String name = text(playerNode, "name");
            String photo = text(playerNode, "photo"); // ✅ real photo URL
            String position = text(gamesNode, "position");
            String nationality = text(playerNode, "nationality");
            Integer age = intOrNull(playerNode, "age");

            // Team info
            TeamSummaryResponse team = null;
            if (!teamNode.isMissingNode() && !teamNode.isEmpty()) {
                team = new TeamSummaryResponse(
                        longOrNull(teamNode, "id"),
                        text(teamNode, "name"),
                        null,
                        text(leagueNode, "name"),
                        text(teamNode, "logo") // ✅ real team logo
                );
            }

            // Competition info
            CompetitionSummaryResponse competition = null;
            if (!leagueNode.isMissingNode() && !leagueNode.isEmpty()) {
                competition = new CompetitionSummaryResponse(
                        longOrNull(leagueNode, "id"),
                        text(leagueNode, "name"),
                        text(leagueNode, "country"),
                        text(leagueNode, "logo") // ✅ real league logo
                );
            }

            // Stats
            int appearances = intOrZero(gamesNode, "appearences"); // api-football typo
            int goals = intOrZero(goalsNode, "total");
            int assists = intOrZero(goalsNode, "assists");
            int minutes = intOrZero(gamesNode, "minutes");
            int yellowCards = intOrZero(cardsNode, "yellow");
            int redCards = intOrZero(cardsNode, "red");
            BigDecimal rating = decimalOrZero(gamesNode, "rating");

            String teamName = team != null ? team.getName() : null;
            String description = buildDescription(name, position, teamName);

            PlayerResponse player = new PlayerResponse();
            player.setId(playerId);
            player.setName(name);
            player.setFirstname(text(playerNode, "firstname"));
            player.setLastname(text(playerNode, "lastname"));
            player.setAge(age);
            player.setNationality(nationality);
            player.setPosition(position);
            player.setPhoto(photo);   // ✅ photo synced
            player.setImage(photo);   // ✅ image synced
            player.setTeamSummary(team);
            player.setCompetition(competition);
            player.setTeam(teamName);
            player.setClub(teamName);
            player.setLeague(competition != null ? competition.getName() : null);
            player.setMarketValue(0L); // free tier doesn't have market values
            player.setAppearances(appearances);
            player.setGoalsScored(goals);
            player.setAssists(assists);
            player.setMinutesPlayed(minutes);
            player.setYellowCards(yellowCards);
            player.setRedCards(redCards);
            player.setRating(rating);
            player.setSpecialAbility("");
            player.setDescription(description);

            return player;
        } catch (Exception e) {
            log.warn("Failed to parse player: {}", e.getMessage());
            return null;
        }
    }

    /**
     * When the same player appears in multiple pages/leagues,
     * keep the entry with more complete stats.
     */
    private PlayerResponse mergePlayer(PlayerResponse existing, PlayerResponse incoming) {
        // Prefer entry with a photo
        if (existing.getPhoto() == null && incoming.getPhoto() != null) {
            existing.setPhoto(incoming.getPhoto());
            existing.setImage(incoming.getPhoto());
        }
        // Prefer higher appearances
        if (safeInt(incoming.getAppearances()) > safeInt(existing.getAppearances())) {
            existing.setAppearances(incoming.getAppearances());
            existing.setGoalsScored(incoming.getGoalsScored());
            existing.setAssists(incoming.getAssists());
            existing.setMinutesPlayed(incoming.getMinutesPlayed());
            existing.setRating(incoming.getRating());
        }
        return existing;
    }

    private JsonNode get(String path, Map<String, String> params) throws IOException {
        String query = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path + (query.isEmpty() ? "" : "?" + query)))
                .header("x-apisports-key", apiKey)
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request interrupted", e);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("API returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode errors = root.path("errors");
        if (!errors.isMissingNode() && !errors.isNull() && errors.size() > 0) {
            throw new IOException("API errors: " + errors);
        }

        return root;
    }

    private String buildDescription(String name, String position, String team) {
        String safeName = name != null ? name : "This player";
        String safePos = position != null ? position.toLowerCase() : "footballer";
        String safeTeam = team != null ? team : "club football";
        return safeName + " is a " + safePos + " currently playing for " + safeTeam + ".";
    }

    private String text(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asText();
    }

    private Long longOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asLong();
    }

    private Integer intOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asInt();
    }

    private int intOrZero(JsonNode node, String field) {
        Integer v = intOrNull(node, field);
        return v != null ? v : 0;
    }

    private BigDecimal decimalOrZero(JsonNode node, String field) {
        String v = text(node, field);
        if (v == null || v.isBlank() || "null".equalsIgnoreCase(v)) return BigDecimal.ZERO;
        try { return new BigDecimal(v); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private int safeInt(Integer v) { return v != null ? v : 0; }

    private String encode(String v) { return URLEncoder.encode(v, StandardCharsets.UTF_8); }
}
