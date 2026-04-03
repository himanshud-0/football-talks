package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.service.ApiFootballStandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/standings")
public class StandingsController {

    private final ApiFootballStandingsService standingsService;

    public StandingsController(ApiFootballStandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping
    public ResponseEntity<?> getStandings(
            @RequestParam(defaultValue = "39") int league,
            @RequestParam(required = false) Integer season) {
        if (!standingsService.isConfigured()) {
            return ResponseEntity.ok(List.of());
        }
        try {
            return ResponseEntity.ok(standingsService.getStandings(league, season));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
