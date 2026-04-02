package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.PlayerDetailResponse;
import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.service.ApiFootballPlayerService;
import com.footballtalks.footballtalks.service.TransferMarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    private final TransferMarketService transferMarketService;
    private final ApiFootballPlayerService apiFootballPlayerService;

    public PlayerController(TransferMarketService transferMarketService,
                            ApiFootballPlayerService apiFootballPlayerService) {
        this.transferMarketService = transferMarketService;
        this.apiFootballPlayerService = apiFootballPlayerService;
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        return ResponseEntity.ok("API configured: " + apiFootballPlayerService.isConfigured());
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getPlayers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long competitionId,
            @RequestParam(required = false) Integer league,
            @RequestParam(required = false) Integer season,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) Integer limit,
            @RequestParam(defaultValue = "false") boolean refresh) {
        if (apiFootballPlayerService.isConfigured()) {
            try {
                List<PlayerResponse> players = apiFootballPlayerService.getPlayers(
                        search,
                        position,
                        teamId,
                        competitionId,
                        sortBy,
                        direction,
                        limit,
                        league,
                        season,
                        refresh
                );
                return ResponseEntity.ok()
                        .header("X-Players-Source", "api-football")
                        .header(
                                "X-Players-Persisted-Count",
                                String.valueOf(apiFootballPlayerService.getLastPersistedCount(league, competitionId, season))
                        )
                        .body(players);
            } catch (RuntimeException exception) {
                log.warn("Falling back to local players after API-Football fetch failed", exception);
                return ResponseEntity.ok()
                        .header("X-Players-Source", "local-fallback")
                        .header("X-Players-Fallback-Reason", exception.getMessage())
                        .body(transferMarketService.getPlayers(search, position, teamId, competitionId, sortBy, direction, limit));
            }
        }

        return ResponseEntity.ok()
                .header("X-Players-Source", "local")
                .body(transferMarketService.getPlayers(search, position, teamId, competitionId, sortBy, direction, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDetailResponse> getPlayer(@PathVariable Long id,
                                                          @RequestParam(required = false) Integer league,
                                                          @RequestParam(required = false) Integer season,
                                                          @RequestParam(defaultValue = "false") boolean refresh) {
        if (apiFootballPlayerService.isConfigured()) {
            try {
                return ResponseEntity.ok()
                        .header("X-Players-Source", "api-football")
                        .body(apiFootballPlayerService.getPlayer(id, league, season, refresh));
            } catch (RuntimeException exception) {
                log.warn("Falling back to local player detail after API-Football fetch failed", exception);
                return ResponseEntity.ok()
                        .header("X-Players-Source", "local-fallback")
                        .header("X-Players-Fallback-Reason", exception.getMessage())
                        .body(transferMarketService.getPlayer(id));
            }
        }

        return ResponseEntity.ok()
                .header("X-Players-Source", "local")
                .body(transferMarketService.getPlayer(id));
    }
}