package com.footballtalks.footballtalks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class ApiFootballFixturesService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final int defaultSeason;
    private final Map<String, CachedFixtures> cache = new HashMap<>();
    private final long cacheTtlSeconds = 1800; // 30 mins

    public ApiFootballFixturesService(
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

    public List<Map<String, Object>> getFixtures(int league, Integer season, String type) {
        int resolvedSeason = season != null ? season : defaultSeason;
        String cacheKey = league + ":" + resolvedSeason + ":" + type;

        CachedFixtures cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheTtlSeconds)) {
            return cached.fixtures();
        }

        List<Map<String, Object>> fixtures = fetchFixtures(league, resolvedSeason, type);
        cache.put(cacheKey, new CachedFixtures(fixtures, Instant.now()));
        return fixtures;
    }

    private List<Map<String, Object>> fetchFixtures(int league, int season, String type) {
        try {
            String url;
            if ("live".equals(type)) {
                url = baseUrl + "/fixtures?live=all&league=" + league;
            } else if ("last".equals(type)) {
                url = baseUrl + "/fixtures?league=" + league + "&season=" + season + "&last=10";
            } else {
                url = baseUrl + "/fixtures?league=" + league + "&season=" + season + "&next=10";
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-apisports-key", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode fixturesArray = root.path("response");

            List<Map<String, Object>> result = new ArrayList<>();
            if (fixturesArray.isArray()) {
                for (JsonNode item : fixturesArray) {
                    JsonNode fixture = item.path("fixture");
                    JsonNode teams = item.path("teams");
                    JsonNode goals = item.path("goals");
                    JsonNode score = item.path("score");

                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fixtureId", fixture.path("id").asLong());
                    row.put("date", fixture.path("date").asText());
                    row.put("status", fixture.path("status").path("short").asText());
                    row.put("statusLong", fixture.path("status").path("long").asText());
                    row.put("elapsed", fixture.path("status").path("elapsed").asInt(0));
                    row.put("venue", fixture.path("venue").path("name").asText(""));

                    Map<String, Object> homeTeam = new LinkedHashMap<>();
                    homeTeam.put("id", teams.path("home").path("id").asLong());
                    homeTeam.put("name", teams.path("home").path("name").asText());
                    homeTeam.put("logo", teams.path("home").path("logo").asText());
                    homeTeam.put("winner", teams.path("home").path("winner").asBoolean(false));
                    row.put("homeTeam", homeTeam);

                    Map<String, Object> awayTeam = new LinkedHashMap<>();
                    awayTeam.put("id", teams.path("away").path("id").asLong());
                    awayTeam.put("name", teams.path("away").path("name").asText());
                    awayTeam.put("logo", teams.path("away").path("logo").asText());
                    awayTeam.put("winner", teams.path("away").path("winner").asBoolean(false));
                    row.put("awayTeam", awayTeam);

                    row.put("homeGoals", goals.path("home").isNull() ? null : goals.path("home").asInt());
                    row.put("awayGoals", goals.path("away").isNull() ? null : goals.path("away").asInt());
                    result.add(row);
                }
            }
            return result;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch fixtures: " + e.getMessage(), e);
        }
    }

    private record CachedFixtures(List<Map<String, Object>> fixtures, Instant fetchedAt) {
        boolean isExpired(long ttlSeconds) {
            return fetchedAt.plusSeconds(ttlSeconds).isBefore(Instant.now());
        }
    }
}
