package com.footballtalks.footballtalks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballtalks.footballtalks.dto.CompetitionSummaryResponse;
import com.footballtalks.footballtalks.dto.PlayerDetailResponse;
import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.dto.TeamSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiFootballPlayerService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiFootballPersistenceService apiFootballPersistenceService;
    private final String baseUrl;
    private final String apiKey;
    private final int defaultLeague;
    private final List<Integer> defaultLeagues;
    private final int defaultSeason;
    private final int maxPage;
    private final int maxTeams;
    private final boolean persistOnFetch;
    private final long pageDelayMs;
    private final long cacheTtlSeconds;
    private final Map<String, CachedPlayers> playersCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> persistedPlayerCounts = new ConcurrentHashMap<>();

    public ApiFootballPlayerService(ObjectMapper objectMapper,
                                    ApiFootballPersistenceService apiFootballPersistenceService,
                                    @Value("${api.football.base-url:https://v3.football.api-sports.io}") String baseUrl,
                                    @Value("${api.football.key:}") String apiKey,
                                    @Value("${api.football.default-league:39}") int defaultLeague,
                                    @Value("${api.football.default-leagues:39,140,78,135,61}") String defaultLeagues,
                                    @Value("${api.football.default-season:2024}") int defaultSeason,
                                    @Value("${api.football.max-page:3}") int maxPage,
                                    @Value("${api.football.max-teams:20}") int maxTeams,
                                    @Value("${api.football.persist-on-fetch:true}") boolean persistOnFetch,
                                    @Value("${api.football.page-delay-ms:250}") long pageDelayMs,
                                    @Value("${api.football.cache-ttl-seconds:900}") long cacheTtlSeconds) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.objectMapper = objectMapper;
        this.apiFootballPersistenceService = apiFootballPersistenceService;
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.apiKey = apiKey;
        this.defaultLeague = defaultLeague;
        this.defaultLeagues = parseLeagueList(defaultLeagues, defaultLeague);
        this.defaultSeason = defaultSeason;
        this.maxPage = Math.max(1, maxPage);
        this.maxTeams = Math.max(1, maxTeams);
        this.persistOnFetch = persistOnFetch;
        this.pageDelayMs = Math.max(0L, pageDelayMs);
        this.cacheTtlSeconds = Math.max(30L, cacheTtlSeconds);
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public List<PlayerResponse> getPlayers(@Nullable String search,
                                           @Nullable String position,
                                           @Nullable Long teamId,
                                           @Nullable Long competitionId,
                                           @Nullable String sortBy,
                                           @Nullable String direction,
                                           @Nullable Integer limit,
                                           @Nullable Integer league,
                                           @Nullable Integer season,
                                           boolean refresh) {
        List<Integer> resolvedLeagues = resolveLeagues(league, competitionId);
        int resolvedSeason = season != null ? season : defaultSeason;
        int resolvedLimit = limit == null || limit < 1 ? Integer.MAX_VALUE : limit;

        return getAllPlayersForLeagues(resolvedLeagues, resolvedSeason, refresh)
                .stream()
                .filter(player -> matchesPlayer(player, search, position, teamId))
                .sorted(buildPlayerComparator(sortBy, direction))
                .limit(resolvedLimit)
                .toList();
    }

    public PlayerDetailResponse getPlayer(long playerId,
                                          @Nullable Integer league,
                                          @Nullable Integer season,
                                          boolean refresh) {
        List<Integer> resolvedLeagues = resolveLeagues(league, null);
        int resolvedSeason = season != null ? season : defaultSeason;

        PlayerResponse player = getAllPlayersForLeagues(resolvedLeagues, resolvedSeason, refresh)
                .stream()
                .filter(candidate -> Objects.equals(candidate.getId(), playerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));

        PlayerDetailResponse response = new PlayerDetailResponse();
        response.setId(player.getId());
        response.setName(player.getName());
        response.setFirstname(player.getFirstname());
        response.setLastname(player.getLastname());
        response.setAge(player.getAge());
        response.setNationality(player.getNationality());
        response.setPosition(player.getPosition());
        response.setMarketValue(player.getMarketValue());
        response.setTeamSummary(player.getTeamSummary());
        response.setCompetition(player.getCompetition());
        response.setTeam(player.getTeam());
        response.setClub(player.getClub());
        response.setLeague(player.getLeague());
        response.setImage(player.getImage());
        response.setPhoto(player.getPhoto());
        response.setDescription(player.getDescription());
        response.setAppearances(player.getAppearances());
        response.setGoalsScored(player.getGoalsScored());
        response.setAssists(player.getAssists());
        response.setMinutesPlayed(player.getMinutesPlayed());
        response.setYellowCards(player.getYellowCards());
        response.setRedCards(player.getRedCards());
        response.setRating(player.getRating());
        response.setSpecialAbility(player.getSpecialAbility());
        return response;
    }

    public int getLastPersistedCount(int league, int season) {
        return persistedPlayerCounts.getOrDefault(league + ":" + season, 0);
    }

    public int getLastPersistedCount(@Nullable Integer league,
                                     @Nullable Long competitionId,
                                     @Nullable Integer season) {
        int resolvedSeason = season != null ? season : defaultSeason;
        return resolveLeagues(league, competitionId)
                .stream()
                .mapToInt(resolvedLeague -> getLastPersistedCount(resolvedLeague, resolvedSeason))
                .sum();
    }

    private List<PlayerResponse> getAllPlayersForLeagues(List<Integer> leagues,
                                                         int season,
                                                         boolean refresh) {
        LinkedHashMap<Long, PlayerResponse> mergedPlayers = new LinkedHashMap<>();
        for (Integer league : leagues) {
            mergePlayers(mergedPlayers, getAllPlayersForLeague(league, season, refresh));
        }
        return new ArrayList<>(mergedPlayers.values());
    }

    private List<PlayerResponse> getAllPlayersForLeague(int league,
                                                        int season,
                                                        boolean refresh) {
        String cacheKey = league + ":" + season;
        CachedPlayers cachedPlayers = playersCache.get(cacheKey);
        if (!refresh && cachedPlayers != null && !cachedPlayers.isExpired(cacheTtlSeconds)) {
            return cachedPlayers.players();
        }

        List<PlayerResponse> players = fetchAllPlayers(league, season);
        playersCache.put(cacheKey, new CachedPlayers(players, Instant.now()));
        return players;
    }

    private List<PlayerResponse> fetchAllPlayers(int league, int season) {
        try {
            LinkedHashMap<Long, PlayerResponse> mergedPlayers = new LinkedHashMap<>();
            CompetitionSummaryResponse competition = null;

            for (TeamSummaryResponse team : fetchLeagueTeams(league, season)) {
                if (team.getId() == null) {
                    continue;
                }

                TeamPlayerFetchResult teamResult = fetchPlayersForTeam(team, season);
                if (competition == null) {
                    competition = teamResult.competition();
                }
                mergePlayers(mergedPlayers, teamResult.players());

                if (teamResult.truncated()) {
                    mergePlayers(mergedPlayers, fetchSquadPlayers(team, competition));
                }
            }

            if (mergedPlayers.isEmpty()) {
                mergePlayers(mergedPlayers, fetchLeaguePlayersFallback(league, season));
            }

            List<PlayerResponse> allPlayers = new ArrayList<>(mergedPlayers.values());

            if (persistOnFetch) {
                int persistedCount = apiFootballPersistenceService.persistPlayers(allPlayers, String.valueOf(season));
                persistedPlayerCounts.put(league + ":" + season, persistedCount);
            }

            return allPlayers;
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch players from API-Football: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Player fetch interrupted: " + e.getMessage(), e);
        }
    }

    private List<TeamSummaryResponse> fetchLeagueTeams(int league,
                                                       int season) throws IOException {
        JsonNode root = executeGet("/teams", Map.of(
                "league", String.valueOf(league),
                "season", String.valueOf(season)
        ));

        List<TeamSummaryResponse> teams = new ArrayList<>();
        JsonNode response = root.path("response");
        if (!response.isArray()) {
            return teams;
        }

        for (JsonNode item : response) {
            if (teams.size() >= maxTeams) {
                break;
            }

            JsonNode teamNode = item.path("team");
            JsonNode leagueNode = item.path("league");
            teams.add(new TeamSummaryResponse(
                    longOrNull(teamNode, "id"),
                    text(teamNode, "name"),
                    text(leagueNode, "country"),
                    text(leagueNode, "name"),
                    text(teamNode, "logo")
            ));
        }

        return teams;
    }

    private TeamPlayerFetchResult fetchPlayersForTeam(TeamSummaryResponse team,
                                                      int season) throws IOException, InterruptedException {
        List<PlayerResponse> players = new ArrayList<>();
        CompetitionSummaryResponse competition = null;
        int currentPage = 1;
        int totalPages = 1;
        int reportedPages = 1;

        while (currentPage <= totalPages) {
            JsonNode root = executeGet("/players", Map.of(
                    "team", String.valueOf(team.getId()),
                    "season", String.valueOf(season),
                    "page", String.valueOf(currentPage)
            ));

            JsonNode paging = root.path("paging");
            reportedPages = Math.max(reportedPages, paging.path("total").asInt(1));
            totalPages = Math.min(reportedPages, maxPage);

            JsonNode response = root.path("response");
            if (response.isArray()) {
                for (JsonNode item : response) {
                    PlayerResponse player = toPlayerResponse(item);
                    if (player.getTeamSummary() == null) {
                        player.setTeamSummary(team);
                        player.setTeam(team.getName());
                        player.setClub(team.getName());
                        player.setLeague(team.getLeague());
                    }

                    if (competition == null && player.getCompetition() != null) {
                        competition = player.getCompetition();
                    }

                    players.add(player);
                }
            }

            currentPage++;
            if (currentPage <= totalPages && pageDelayMs > 0) {
                Thread.sleep(pageDelayMs);
            }
        }

        return new TeamPlayerFetchResult(players, competition, reportedPages > maxPage);
    }

    private List<PlayerResponse> fetchSquadPlayers(TeamSummaryResponse team,
                                                   @Nullable CompetitionSummaryResponse competition) throws IOException {
        JsonNode root = executeGet("/players/squads", Map.of("team", String.valueOf(team.getId())));

        List<PlayerResponse> players = new ArrayList<>();
        JsonNode response = root.path("response");
        if (!response.isArray()) {
            return players;
        }

        for (JsonNode squadNode : response) {
            JsonNode playersNode = squadNode.path("players");
            if (!playersNode.isArray()) {
                continue;
            }

            for (JsonNode playerNode : playersNode) {
                PlayerResponse player = new PlayerResponse();
                player.setId(longOrNull(playerNode, "id"));
                player.setName(text(playerNode, "name"));
                player.setFirstname(text(playerNode, "firstname"));
                player.setLastname(text(playerNode, "lastname"));
                player.setAge(intOrNull(playerNode, "age"));
                player.setNationality(text(playerNode, "nationality"));
                player.setPosition(text(playerNode, "position"));
                player.setMarketValue(0L);
                player.setTeamSummary(team);
                player.setCompetition(competition);
                player.setTeam(team.getName());
                player.setClub(team.getName());
                player.setLeague(team.getLeague());
                player.setImage(text(playerNode, "photo"));
                player.setPhoto(text(playerNode, "photo"));
                player.setDescription(buildDescription(player.getName(), player.getPosition(), team.getName()));
                player.setAppearances(0);
                player.setGoalsScored(0);
                player.setAssists(0);
                player.setMinutesPlayed(0);
                player.setYellowCards(0);
                player.setRedCards(0);
                player.setRating(BigDecimal.ZERO);
                player.setSpecialAbility("");
                players.add(player);
            }
        }

        return players;
    }

    private List<PlayerResponse> fetchLeaguePlayersFallback(int league,
                                                            int season) throws IOException, InterruptedException {
        List<PlayerResponse> players = new ArrayList<>();
        int currentPage = 1;
        int totalPages = 1;

        while (currentPage <= totalPages) {
            JsonNode root = executeGet("/players", Map.of(
                    "league", String.valueOf(league),
                    "season", String.valueOf(season),
                    "page", String.valueOf(currentPage)
            ));

            JsonNode paging = root.path("paging");
            totalPages = Math.min(paging.path("total").asInt(totalPages), maxPage);

            JsonNode response = root.path("response");
            if (response.isArray()) {
                for (JsonNode item : response) {
                    players.add(toPlayerResponse(item));
                }
            }

            currentPage++;
            if (currentPage <= totalPages && pageDelayMs > 0) {
                Thread.sleep(pageDelayMs);
            }
        }

        return players;
    }

    private void mergePlayers(Map<Long, PlayerResponse> playersById,
                              List<PlayerResponse> incomingPlayers) {
        for (PlayerResponse incomingPlayer : incomingPlayers) {
            Long playerId = incomingPlayer.getId();
            if (playerId == null) {
                continue;
            }

            PlayerResponse existingPlayer = playersById.get(playerId);
            if (existingPlayer == null) {
                playersById.put(playerId, incomingPlayer);
                continue;
            }

            playersById.put(playerId, mergePlayer(existingPlayer, incomingPlayer));
        }
    }

    private PlayerResponse mergePlayer(PlayerResponse left,
                                       PlayerResponse right) {
        PlayerResponse primary = richnessScore(right) >= richnessScore(left) ? right : left;
        PlayerResponse secondary = primary == right ? left : right;

        if (primary.getFirstname() == null) primary.setFirstname(secondary.getFirstname());
        if (primary.getLastname() == null) primary.setLastname(secondary.getLastname());
        if (primary.getAge() == null) primary.setAge(secondary.getAge());
        if (primary.getNationality() == null) primary.setNationality(secondary.getNationality());
        if (primary.getPosition() == null) primary.setPosition(secondary.getPosition());
        if (primary.getTeamSummary() == null) primary.setTeamSummary(secondary.getTeamSummary());
        if (primary.getCompetition() == null) primary.setCompetition(secondary.getCompetition());
        if (primary.getTeam() == null) primary.setTeam(secondary.getTeam());
        if (primary.getClub() == null) primary.setClub(secondary.getClub());
        if (primary.getLeague() == null) primary.setLeague(secondary.getLeague());
        if (primary.getImage() == null) primary.setImage(secondary.getImage());
        if (primary.getPhoto() == null) primary.setPhoto(secondary.getPhoto());
        if (primary.getDescription() == null) primary.setDescription(secondary.getDescription());
        if (safeLong(primary.getMarketValue()) == 0L) primary.setMarketValue(secondary.getMarketValue());
        if (safeInt(primary.getAppearances()) == 0) primary.setAppearances(secondary.getAppearances());
        if (safeInt(primary.getGoalsScored()) == 0) primary.setGoalsScored(secondary.getGoalsScored());
        if (safeInt(primary.getAssists()) == 0) primary.setAssists(secondary.getAssists());
        if (safeInt(primary.getMinutesPlayed()) == 0) primary.setMinutesPlayed(secondary.getMinutesPlayed());
        if (safeInt(primary.getYellowCards()) == 0) primary.setYellowCards(secondary.getYellowCards());
        if (safeInt(primary.getRedCards()) == 0) primary.setRedCards(secondary.getRedCards());
        if (safeBigDecimal(primary.getRating()).compareTo(BigDecimal.ZERO) == 0) primary.setRating(secondary.getRating());
        if (primary.getSpecialAbility() == null || primary.getSpecialAbility().isBlank()) {
            primary.setSpecialAbility(secondary.getSpecialAbility());
        }

        return primary;
    }

    private int richnessScore(PlayerResponse player) {
        int score = 0;
        if (safeInt(player.getAppearances()) > 0) score += 5;
        if (safeInt(player.getMinutesPlayed()) > 0) score += 5;
        if (safeInt(player.getGoalsScored()) > 0) score += 4;
        if (safeInt(player.getAssists()) > 0) score += 3;
        if (safeBigDecimal(player.getRating()).compareTo(BigDecimal.ZERO) > 0) score += 3;
        if (player.getCompetition() != null) score += 2;
        if (player.getTeamSummary() != null) score += 2;
        if (player.getPhoto() != null) score += 1;
        return score;
    }

    private JsonNode executeGet(String path, Map<String, String> params) throws IOException {
        String query = params.entrySet()
                .stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
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
            throw new IOException("API-Football returned status " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode errors = root.path("errors");
        if (!errors.isMissingNode() && !errors.isNull() && errors.size() > 0) {
            throw new IOException("API-Football returned errors: " + errors);
        }

        return root;
    }

    private PlayerResponse toPlayerResponse(JsonNode item) {
        JsonNode playerNode = item.path("player");
        JsonNode statsNode = item.path("statistics").isArray() && item.path("statistics").size() > 0
                ? item.path("statistics").get(0)
                : objectMapper.createObjectNode();

        JsonNode teamNode = statsNode.path("team");
        JsonNode leagueNode = statsNode.path("league");
        JsonNode gamesNode = statsNode.path("games");
        JsonNode goalsNode = statsNode.path("goals");
        JsonNode cardsNode = statsNode.path("cards");

        TeamSummaryResponse teamSummary = teamNode.isMissingNode() || teamNode.isEmpty()
                ? null
                : new TeamSummaryResponse(
                longOrNull(teamNode, "id"),
                text(teamNode, "name"),
                null,
                text(leagueNode, "name"),
                text(teamNode, "logo")
        );

        CompetitionSummaryResponse competition = leagueNode.isMissingNode() || leagueNode.isEmpty()
                ? null
                : new CompetitionSummaryResponse(
                longOrNull(leagueNode, "id"),
                text(leagueNode, "name"),
                text(leagueNode, "country"),
                text(leagueNode, "logo")
        );

        String playerName = text(playerNode, "name");
        String teamName = teamSummary != null ? teamSummary.getName() : null;
        String leagueName = competition != null ? competition.getName() : null;
        String position = text(gamesNode, "position");

        PlayerResponse response = new PlayerResponse();
        response.setId(longOrNull(playerNode, "id"));
        response.setName(playerName);
        response.setFirstname(text(playerNode, "firstname"));
        response.setLastname(text(playerNode, "lastname"));
        response.setAge(intOrNull(playerNode, "age"));
        response.setNationality(text(playerNode, "nationality"));
        response.setPosition(position);
        response.setMarketValue(0L);
        response.setTeamSummary(teamSummary);
        response.setCompetition(competition);
        response.setTeam(teamName);
        response.setClub(teamName);
        response.setLeague(leagueName);
        response.setImage(text(playerNode, "photo"));
        response.setPhoto(text(playerNode, "photo"));
        response.setDescription(buildDescription(playerName, position, teamName));
        response.setAppearances(intOrZero(gamesNode, "appearences"));
        response.setGoalsScored(intOrZero(goalsNode, "total"));
        response.setAssists(intOrZero(goalsNode, "assists"));
        response.setMinutesPlayed(intOrZero(gamesNode, "minutes"));
        response.setYellowCards(intOrZero(cardsNode, "yellow"));
        response.setRedCards(intOrZero(cardsNode, "red"));
        response.setRating(decimalOrZero(gamesNode, "rating"));
        response.setSpecialAbility("");
        return response;
    }

    private boolean matchesPlayer(PlayerResponse player,
                                  @Nullable String search,
                                  @Nullable String position,
                                  @Nullable Long teamId) {
        if (teamId != null) {
            Long currentTeamId = player.getTeamSummary() != null ? player.getTeamSummary().getId() : null;
            if (!Objects.equals(teamId, currentTeamId)) {
                return false;
            }
        }

        if (position != null && !position.isBlank()) {
            String normalizedPosition = normalize(position);
            if (!Objects.equals(normalizedPosition, normalize(player.getPosition()))) {
                return false;
            }
        }

        String normalizedSearch = normalize(search);
        if (normalizedSearch == null) {
            return true;
        }

        return contains(player.getName(), normalizedSearch)
                || contains(player.getNationality(), normalizedSearch)
                || contains(player.getPosition(), normalizedSearch)
                || contains(player.getClub(), normalizedSearch)
                || contains(player.getLeague(), normalizedSearch);
    }

    private Comparator<PlayerResponse> buildPlayerComparator(@Nullable String sortBy,
                                                             @Nullable String direction) {
        Map<String, Comparator<PlayerResponse>> comparators = new HashMap<>();
        comparators.put("name", Comparator.comparing(
                player -> safeText(player.getName()),
                String.CASE_INSENSITIVE_ORDER
        ));
        comparators.put("age", Comparator.comparing(player -> safeInt(player.getAge())));
        comparators.put("marketvalue", Comparator.comparing(player -> safeLong(player.getMarketValue())));
        comparators.put("rating", Comparator.comparing(player -> safeBigDecimal(player.getRating())));
        comparators.put("goals", Comparator.comparing(player -> safeInt(player.getGoalsScored())));
        comparators.put("assists", Comparator.comparing(player -> safeInt(player.getAssists())));

        Comparator<PlayerResponse> comparator = comparators.getOrDefault(
                normalize(sortBy),
                comparators.get("name")
        ).thenComparing(player -> safeText(player.getName()), String.CASE_INSENSITIVE_ORDER);

        if (!"asc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private String buildDescription(@Nullable String playerName,
                                    @Nullable String position,
                                    @Nullable String teamName) {
        String safeName = playerName != null && !playerName.isBlank() ? playerName : "This player";
        String safePosition = position != null && !position.isBlank()
                ? position.toLowerCase(Locale.ROOT)
                : "footballer";
        String safeTeam = teamName != null && !teamName.isBlank() ? teamName : "club football";
        return safeName + " is a " + safePosition + " currently listed for " + safeTeam + ".";
    }

    private String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String text(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asText();
    }

    private Integer intOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asInt();
    }

    private Integer intOrZero(JsonNode node, String field) {
        Integer value = intOrNull(node, field);
        return value != null ? value : 0;
    }

    private Long longOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asLong();
    }

    private BigDecimal decimalOrZero(JsonNode node, String field) {
        String value = text(node, field);
        if (value == null || value.isBlank() || "null".equalsIgnoreCase(value)) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            return BigDecimal.ZERO;
        }
    }

    private String normalize(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private List<Integer> resolveLeagues(@Nullable Integer league,
                                         @Nullable Long competitionId) {
        if (league != null) {
            return List.of(league);
        }
        if (competitionId != null) {
            return List.of(competitionId.intValue());
        }
        return defaultLeagues;
    }

    private List<Integer> parseLeagueList(String configuredLeagues,
                                          int fallbackLeague) {
        List<Integer> leagues = configuredLeagues == null
                ? List.of()
                : List.of(configuredLeagues.split(","))
                .stream()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return leagues.isEmpty() ? List.of(fallbackLeague) : leagues;
    }

    private boolean contains(@Nullable String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private String safeText(@Nullable String value) {
        return value != null ? value : "";
    }

    private int safeInt(@Nullable Integer value) {
        return value != null ? value : 0;
    }

    private long safeLong(@Nullable Long value) {
        return value != null ? value : 0L;
    }

    private BigDecimal safeBigDecimal(@Nullable BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private record CachedPlayers(List<PlayerResponse> players, Instant fetchedAt) {
        private boolean isExpired(long ttlSeconds) {
            return fetchedAt.plusSeconds(ttlSeconds).isBefore(Instant.now());
        }
    }

    private record TeamPlayerFetchResult(List<PlayerResponse> players,
                                         @Nullable CompetitionSummaryResponse competition,
                                         boolean truncated) {
    }
}
