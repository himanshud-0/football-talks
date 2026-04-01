package com.footballtalks.footballtalks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.footballtalks.footballtalks.dto.TeamSummaryResponse;
import com.footballtalks.footballtalks.dto.TransferResponse;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ApiFootballTransferService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiFootballPersistenceService apiFootballPersistenceService;
    private final String baseUrl;
    private final String apiKey;
    private final int defaultLeague;
    private final int defaultSeason;
    private final int maxTeams;
    private final boolean persistOnFetch;
    private final long cacheTtlSeconds;
    private final Map<String, CachedTransfers> transferCache = new HashMap<>();

    public ApiFootballTransferService(ObjectMapper objectMapper,
                                      ApiFootballPersistenceService apiFootballPersistenceService,
                                      @Value("${api.football.base-url:https://v3.football.api-sports.io}") String baseUrl,
                                      @Value("${api.football.key:}") String apiKey,
                                      @Value("${api.football.default-league:39}") int defaultLeague,
                                      @Value("${api.football.default-season:2024}") int defaultSeason,
                                      @Value("${api.football.max-teams:20}") int maxTeams,
                                      @Value("${api.football.persist-on-fetch:true}") boolean persistOnFetch,
                                      @Value("${api.football.cache-ttl-seconds:900}") long cacheTtlSeconds) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.objectMapper = objectMapper;
        this.apiFootballPersistenceService = apiFootballPersistenceService;
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.apiKey = apiKey;
        this.defaultLeague = defaultLeague;
        this.defaultSeason = defaultSeason;
        this.maxTeams = Math.max(1, maxTeams);
        this.persistOnFetch = persistOnFetch;
        this.cacheTtlSeconds = Math.max(30L, cacheTtlSeconds);
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public List<TransferResponse> getTransfers(@Nullable String search,
                                               @Nullable String transferSeasonFilter,
                                               @Nullable Integer limit,
                                               @Nullable Integer league,
                                               @Nullable Integer apiSeason,
                                               boolean refresh) {
        int resolvedLeague = league != null ? league : defaultLeague;
        int resolvedApiSeason = apiSeason != null ? apiSeason : defaultSeason;
        int resolvedLimit = limit == null || limit < 1 ? Integer.MAX_VALUE : limit;

        return getLeagueTransfers(resolvedLeague, resolvedApiSeason, refresh)
                .stream()
                .filter(transfer -> matchesTransfer(transfer, search, transferSeasonFilter))
                .sorted(Comparator
                        .comparing(TransferResponse::getTransferDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(transfer -> safeText(transfer.getPlayerName()), String.CASE_INSENSITIVE_ORDER))
                .limit(resolvedLimit)
                .toList();
    }

    private List<TransferResponse> getLeagueTransfers(int league,
                                                      int apiSeason,
                                                      boolean refresh) {
        String cacheKey = league + ":" + apiSeason;
        CachedTransfers cachedTransfers = transferCache.get(cacheKey);
        if (!refresh && cachedTransfers != null && !cachedTransfers.isExpired(cacheTtlSeconds)) {
            return cachedTransfers.transfers();
        }

        List<TransferResponse> transfers = fetchLeagueTransfers(league, apiSeason);
        transferCache.put(cacheKey, new CachedTransfers(transfers, Instant.now()));
        return transfers;
    }

    private List<TransferResponse> fetchLeagueTransfers(int league,
                                                        int apiSeason) {
        try {
            List<TeamSummaryResponse> teams = fetchLeagueTeams(league, apiSeason);
            LinkedHashMap<String, TransferResponse> dedupedTransfers = new LinkedHashMap<>();

            for (TeamSummaryResponse team : teams) {
                if (team.getId() == null) {
                    continue;
                }

                JsonNode root = executeGet("/transfers", Map.of("team", String.valueOf(team.getId())));
                JsonNode response = root.path("response");
                if (!response.isArray()) {
                    continue;
                }

                for (JsonNode playerTransfersNode : response) {
                    JsonNode playerNode = playerTransfersNode.path("player");
                    JsonNode transfersNode = playerTransfersNode.path("transfers");
                    if (!transfersNode.isArray()) {
                        continue;
                    }

                    for (JsonNode transferNode : transfersNode) {
                        TransferResponse transfer = toTransferResponse(playerNode, transferNode);
                        dedupedTransfers.put(buildTransferKey(transfer), transfer);
                    }
                }
            }

            List<TransferResponse> transfers = new ArrayList<>(dedupedTransfers.values());
            if (persistOnFetch) {
                apiFootballPersistenceService.persistTransfers(transfers);
            }
            return transfers;
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch transfers from API-Football: " + e.getMessage(), e);
        }
    }

    private List<TeamSummaryResponse> fetchLeagueTeams(int league,
                                                       int apiSeason) throws IOException {
        JsonNode root = executeGet("/teams", Map.of(
                "league", String.valueOf(league),
                "season", String.valueOf(apiSeason)
        ));

        List<TeamSummaryResponse> teams = new ArrayList<>();
        JsonNode response = root.path("response");
        if (response.isArray()) {
            for (JsonNode item : response) {
                if (teams.size() >= maxTeams) {
                    break;
                }

                JsonNode teamNode = item.path("team");
                teams.add(new TeamSummaryResponse(
                        longOrNull(teamNode, "id"),
                        text(teamNode, "name"),
                        null,
                        null,
                        text(teamNode, "logo")
                ));
            }
        }

        return teams;
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

    private TransferResponse toTransferResponse(JsonNode playerNode,
                                                JsonNode transferNode) {
        JsonNode teamsNode = transferNode.path("teams");
        JsonNode outTeamNode = teamsNode.path("out");
        JsonNode inTeamNode = teamsNode.path("in");
        String transferType = text(transferNode, "type");
        LocalDate transferDate = parseDate(text(transferNode, "date"));

        TeamSummaryResponse fromTeam = buildTeamSummary(outTeamNode);
        TeamSummaryResponse toTeam = buildTeamSummary(inTeamNode);

        TransferResponse response = new TransferResponse();
        response.setId(null);
        response.setPlayerId(longOrNull(playerNode, "id"));
        response.setPlayerName(text(playerNode, "name"));
        response.setPlayerImage(text(playerNode, "photo"));
        response.setPlayerMarketValue(0L);
        response.setFromTeam(fromTeam);
        response.setToTeam(toTeam);
        response.setTransferFee(parseFeeToLong(transferType));
        response.setTransferType(transferType);
        response.setTransferFeeFormatted(formatTransferType(transferType));
        response.setTransferDate(transferDate);
        response.setSeason(resolveSeason(transferDate));
        return response;
    }

    private TeamSummaryResponse buildTeamSummary(JsonNode teamNode) {
        if (teamNode == null || teamNode.isMissingNode() || teamNode.isNull()) {
            return null;
        }

        return new TeamSummaryResponse(
                longOrNull(teamNode, "id"),
                text(teamNode, "name"),
                null,
                null,
                text(teamNode, "logo")
        );
    }

    private boolean matchesTransfer(TransferResponse transfer,
                                    @Nullable String search,
                                    @Nullable String transferSeasonFilter) {
        String normalizedSeason = normalize(transferSeasonFilter);
        if (normalizedSeason != null && !normalizedSeason.equals(normalize(transfer.getSeason()))) {
            return false;
        }

        String normalizedSearch = normalize(search);
        if (normalizedSearch == null) {
            return true;
        }

        return contains(transfer.getPlayerName(), normalizedSearch)
                || contains(transfer.getSeason(), normalizedSearch)
                || contains(transfer.getTransferType(), normalizedSearch)
                || contains(transfer.getFromTeam() != null ? transfer.getFromTeam().getName() : null, normalizedSearch)
                || contains(transfer.getToTeam() != null ? transfer.getToTeam().getName() : null, normalizedSearch);
    }

    private String buildTransferKey(TransferResponse transfer) {
        return safeText(String.valueOf(transfer.getPlayerId()))
                + "|" + safeText(transfer.getTransferDate() != null ? transfer.getTransferDate().toString() : "")
                + "|" + safeText(transfer.getFromTeam() != null && transfer.getFromTeam().getId() != null ? String.valueOf(transfer.getFromTeam().getId()) : "")
                + "|" + safeText(transfer.getToTeam() != null && transfer.getToTeam().getId() != null ? String.valueOf(transfer.getToTeam().getId()) : "")
                + "|" + safeText(transfer.getTransferType());
    }

    private Long parseFeeToLong(@Nullable String transferType) {
        if (transferType == null) {
            return null;
        }

        String normalized = transferType.trim().toUpperCase(Locale.ROOT)
                .replace("EUR", "")
                .replace("€", "")
                .replace(",", "")
                .replace(" ", "");

        try {
            if (normalized.endsWith("M")) {
                return BigDecimal.valueOf(Double.parseDouble(normalized.substring(0, normalized.length() - 1)))
                        .multiply(BigDecimal.valueOf(1_000_000L))
                        .longValue();
            }
            if (normalized.endsWith("K")) {
                return BigDecimal.valueOf(Double.parseDouble(normalized.substring(0, normalized.length() - 1)))
                        .multiply(BigDecimal.valueOf(1_000L))
                        .longValue();
            }
            if (normalized.chars().allMatch(Character::isDigit)) {
                return Long.parseLong(normalized);
            }
        } catch (NumberFormatException ignored) {
            return null;
        }

        return null;
    }

    private String formatTransferType(@Nullable String transferType) {
        if (transferType == null || transferType.isBlank()) {
            return "Undisclosed";
        }

        return transferType;
    }

    private String resolveSeason(@Nullable LocalDate transferDate) {
        if (transferDate == null) {
            return null;
        }

        int startYear = transferDate.getMonthValue() >= 7
                ? transferDate.getYear()
                : transferDate.getYear() - 1;
        int endYear = (startYear + 1) % 100;
        return startYear + "/" + String.format("%02d", endYear);
    }

    private LocalDate parseDate(@Nullable String date) {
        if (date == null || date.isBlank()) {
            return null;
        }

        return LocalDate.parse(date);
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

    private Long longOrNull(JsonNode node, String field) {
        JsonNode child = node.path(field);
        return child.isMissingNode() || child.isNull() ? null : child.asLong();
    }

    private String normalize(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean contains(@Nullable String value, String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private String safeText(@Nullable String value) {
        return value != null ? value : "";
    }

    private record CachedTransfers(List<TransferResponse> transfers, Instant fetchedAt) {
        private boolean isExpired(long ttlSeconds) {
            return fetchedAt.plusSeconds(ttlSeconds).isBefore(Instant.now());
        }
    }
}
