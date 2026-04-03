package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.service.ApiFootballFixturesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fixtures")
public class FixturesController {

    private final ApiFootballFixturesService fixturesService;

    public FixturesController(ApiFootballFixturesService fixturesService) {
        this.fixturesService = fixturesService;
    }

    @GetMapping
    public ResponseEntity<?> getFixtures(
            @RequestParam(defaultValue = "39") int league,
            @RequestParam(required = false) Integer season,
            @RequestParam(defaultValue = "next") String type) { // next, last, live
        if (!fixturesService.isConfigured()) {
            return ResponseEntity.ok(List.of());
        }
        try {
            return ResponseEntity.ok(fixturesService.getFixtures(league, season, type));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
