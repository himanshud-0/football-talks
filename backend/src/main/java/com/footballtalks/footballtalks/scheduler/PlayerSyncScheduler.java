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
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(PlayerSyncScheduler.class);

    private static final List<String> LEAGUES_TO_SYNC = Arrays.asList(
            "PL",   // Premier League
            "PD",   // La Liga
            "SA",   // Serie A
            "BL1",  // Bundesliga
            "FL1",  // Ligue 1
            "CL",   // Champions League
            "DED",  // Eredivisie
            "PPL",  // Primeira Liga
            "BSA",  // Brasileirao
            "ELC"   // Championship
    );

    private static final Map<String, Long> LEAGUE_IDS = new HashMap<>() {{
        put("PL", 2021L);
        put("PD", 2014L);
        put("SA", 2019L);
        put("BL1", 2002L);
        put("FL1", 2015L);
        put("CL", 2001L);
        put("DED", 2003L);
        put("PPL", 2017L);
        put("BSA", 2013L);
        put("ELC", 2016L);
    }};

    private static final Map<String, String> LEAGUE_COUNTRIES = new HashMap<>() {{
        put("PL", "England");
        put("PD", "Spain");
        put("SA", "Italy");
        put("BL1", "Germany");
        put("FL1", "France");
        put("CL", "Europe");
        put("DED", "Netherlands");
        put("PPL", "Portugal");
        put("BSA", "Brazil");
        put("ELC", "England");
    }};

    private static final Map<String, String> LEAGUE_NAMES = new HashMap<>() {{
        put("PL", "Premier League");
        put("PD", "La Liga");
        put("SA", "Serie A");
        put("BL1", "Bundesliga");
        put("FL1", "Ligue 1");
        put("CL", "UEFA Champions League");
        put("DED", "Eredivisie");
        put("PPL", "Primeira Liga");
        put("BSA", "Brasileirao");
        put("ELC", "Championship");
    }};

    private final ApiFootballPlayerService apiFootballPlayerService;
    private final ApiFootballPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private final String footballDataApiKey;
    private final HttpClient httpClient;
    private int currentIndex = 0;

    public PlayerSyncScheduler(ApiFootballPlayerService apiFootballPlayerService,
                                ApiFootballPersistenceService persistenceService,
                                ObjectMapper objectMapper,
                                @Value("${football.data.api.key:}") String footballDataApiKey) {
        this.apiFootballPlayerService = apiFootballPlayerService;
        this.persistenceService = persistenceService;
        this.objectMapper = objectMapper;
        this.footballDataApiKey = footballDataApiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    public void syncNextLeague() {
        if (footballDataApiKey == null || footballDataApiKey.isBlank()) {
            log.warn("FOOTBALL_DATA_API_KEY not configured — skipping sync");
            return;
        }

        String leagueCode = LEAGUES_TO_SYNC.get(currentIndex);
        currentIndex = (currentIndex + 1) % LEAGUES_TO_SYNC.size();

        log.info("Starting daily sync for league {}", leagueCode);

        try {
            int totalFetched = fetchAndPersistLeague(leagueCode);
            log.info("Synced {} players for league {}", totalFetched, leagueCode);
        } catch (Exception e) {
            log.error("Failed to sync league {}: {}", leagueCode, e.getMessage(), e);
        }
    }

    private int fetchAndPersistLeague(String leagueCode) throws Exception {
        Long competitionId = LEAGUE_IDS.get(leagueCode);
        String leagueName = LEAGUE_NAMES.get(leagueCode);
        String country = LEAGUE_COUNTRIES.get(leagueCode);

        // Fetch all teams with squads in one request
        String url = String.format("https://api.football-data.org/v4/competitions/%s/teams?season=2024", leagueCode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Auth-Token", footballDataApiKey)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.warn("API returned {} for league {}: {}", response.statusCode(), leagueCode, response.body());
            return 0;
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode competitionNode = root.path("competition");
        JsonNode teamsArray = root.path("teams");

        if (!teamsArray.isArray() || teamsArray.size() == 0) {
            log.warn("No teams found for league {}", leagueCode);
            return 0;
        }

        CompetitionSummaryResponse competition = new CompetitionSummaryResponse(
                competitionId,
                leagueName,
                country,
                competitionNode.path("emblem").asText(null)
        );

        int totalFetched = 0;

        for (JsonNode teamNode : teamsArray) {
            long teamId = teamNode.path("id").asLong();
            String teamName = teamNode.path("name").asText(null);
            String teamLogo = teamNode.path("crest").asText(null);

            TeamSummaryResponse team = new TeamSummaryResponse(
                    teamId, teamName, country, leagueName, teamLogo
            );

            // Try to get squad from team node first
            JsonNode squadArray = teamNode.path("squad");
            List<PlayerResponse> players = new ArrayList<>();

            if (squadArray.isArray() && squadArray.size() > 0) {
                for (JsonNode playerNode : squadArray) {
                    PlayerResponse player = parsePlayer(playerNode, team, competition);
                    if (player != null) players.add(player);
                }
            } else {
                // Fetch squad separately with rate limit delay
                Thread.sleep(6100); // 10 req/min = 1 per 6 seconds
                players = fetchTeamSquad(teamId, team, competition);
            }

            if (!players.isEmpty()) {
                int persisted = persistenceService.persistPlayers(players, "2024");
                totalFetched += persisted;
                log.info("{} - {}: {} players", leagueCode, teamName, persisted);
            }
        }

        return totalFetched;
    }

    private List<PlayerResponse> fetchTeamSquad(long teamId,
                                                  TeamSummaryResponse team,
                                                  CompetitionSummaryResponse competition) throws Exception {
        String url = String.format("https://api.football-data.org/v4/teams/%d", teamId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Auth-Token", footballDataApiKey)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.warn("Team {} returned status {}", teamId, response.statusCode());
            return new ArrayList<>();
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode squadArray = root.path("squad");

        List<PlayerResponse> players = new ArrayList<>();
        if (squadArray.isArray()) {
            for (JsonNode playerNode : squadArray) {
                PlayerResponse player = parsePlayer(playerNode, team, competition);
                if (player != null) players.add(player);
            }
        }

        return players;
    }

    private PlayerResponse parsePlayer(JsonNode playerNode,
                                        TeamSummaryResponse team,
                                        CompetitionSummaryResponse competition) {
        try {
            long playerId = playerNode.path("id").asLong();
            if (playerId == 0) return null;

            String name = playerNode.path("name").asText(null);
            String position = mapPosition(playerNode.path("position").asText(null));
            String nationality = playerNode.path("nationality").asText(null);
            String dateOfBirth = playerNode.path("dateOfBirth").asText(null);
            Integer age = calculateAge(dateOfBirth);

            PlayerResponse player = new PlayerResponse();
            player.setId(playerId);
            player.setName(name);
            player.setFirstname(extractFirstName(name));
            player.setLastname(extractLastName(name));
            player.setAge(age);
            player.setNationality(nationality);
            player.setPosition(position);
            player.setPhoto(null);
            player.setImage(null);
            player.setTeamSummary(team);
            player.setCompetition(competition);
            player.setTeam(team != null ? team.getName() : null);
            player.setClub(team != null ? team.getName() : null);
            player.setLeague(competition != null ? competition.getName() : null);
            player.setMarketValue(0L);
            player.setAppearances(0);
            player.setGoalsScored(0);
            player.setAssists(0);
            player.setMinutesPlayed(0);
            player.setYellowCards(0);
            player.setRedCards(0);
            player.setRating(BigDecimal.ZERO);
            player.setSpecialAbility("");
            player.setDescription(name + " is a " +
                    (position != null ? position.toLowerCase() : "footballer") +
                    (team != null ? " playing for " + team.getName() : "") + ".");

            return player;
        } catch (Exception e) {
            log.warn("Failed to parse player: {}", e.getMessage());
            return null;
        }
    }

    private String mapPosition(String position) {
        if (position == null) return null;
        return switch (position) {
            case "Goalkeeper" -> "Goalkeeper";
            case "Centre-Back", "Left-Back", "Right-Back" -> "Defender";
            case "Defensive Midfield", "Central Midfield", "Attacking Midfield",
                    "Left Midfield", "Right Midfield" -> "Midfielder";
            case "Centre-Forward", "Left Winger", "Right Winger", "Secondary Striker" -> "Attacker";
            default -> position;
        };
    }

    private Integer calculateAge(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isBlank()) return null;
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth.substring(0, 10));
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractFirstName(String fullName) {
        if (fullName == null) return null;
        String[] parts = fullName.split(" ");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null) return null;
        String[] parts = fullName.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : null;
    }
}