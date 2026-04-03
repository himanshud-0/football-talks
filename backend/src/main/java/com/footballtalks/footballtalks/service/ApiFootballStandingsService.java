package com.footballtalks.footballtalks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ApiFootballStandingsService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final int defaultSeason;
    private final Map<String, CachedStandings> cache = new HashMap<>();
    private final long cacheTtlSeconds = 3600; // 1 hour

    public ApiFootballStandingsService(
            ObjectMapper objectMapper,
            @Value("${api.football.base-url:https://v3.football.api-sports.io}") String baseUrl,
            @Value("${api.football.key:}") String apiKey,
            @Value("${api.football.default-season:2024}") int defaultSeason) {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.defaultSeason = defaultSeason;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public List<Map<String, Object>> getStandings(int league, Integer season) {
        int resolvedSeason = season != null ? season : defaultSeason;
        String cacheKey = league + ":" + resolvedSeason;

        CachedStandings cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheTtlSeconds)) {
            return cached.standings();
        }

        List<Map<String, Object>> standings = fetchStandings(league, resolvedSeason);
        cache.put(cacheKey, new CachedStandings(standings, Instant.now()));
        return standings;
    }

    private List<Map<String, Object>> fetchStandings(int league, int season) {
        try {
            String url = baseUrl + "/standings?league=" + league + "&season=" + season;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-apisports-key", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode standingsArray = root.path("response").path(0)
                    .path("league").path("standings").path(0);

            List<Map<String, Object>> result = new ArrayList<>();
            if (standingsArray.isArray()) {
                for (JsonNode entry : standingsArray) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("rank", entry.path("rank").asInt());
                    row.put("teamId", entry.path("team").path("id").asLong());
                    row.put("teamName", entry.path("team").path("name").asText());
                    row.put("teamLogo", entry.path("team").path("logo").asText());
                    row.put("played", entry.path("all").path("played").asInt());
                    row.put("win", entry.path("all").path("win").asInt());
                    row.put("draw", entry.path("all").path("draw").asInt());
                    row.put("lose", entry.path("all").path("lose").asInt());
                    row.put("goalsFor", entry.path("all").path("goals").path("for").asInt());
                    row.put("goalsAgainst", entry.path("all").path("goals").path("against").asInt());
                    row.put("goalDifference", entry.path("goalsDiff").asInt());
                    row.put("points", entry.path("points").asInt());
                    row.put("form", entry.path("form").asText());
                    row.put("description", entry.path("description").asText(""));
                    result.add(row);
                }
            }
            return result;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch standings: " + e.getMessage(), e);
        }
    }

    private record CachedStandings(List<Map<String, Object>> standings, Instant fetchedAt) {
        boolean isExpired(long ttlSeconds) {
            return fetchedAt.plusSeconds(ttlSeconds).isBefore(Instant.now());
        }
    }
}
