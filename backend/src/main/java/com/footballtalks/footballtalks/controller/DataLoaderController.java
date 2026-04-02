package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.service.ApiFootballPersistenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class DataLoaderController {

    private final ApiFootballPersistenceService persistenceService;

    public DataLoaderController(ApiFootballPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @PostMapping("/load")
    public String loadPlayers(@RequestBody List<PlayerResponse> players) {
        int count = persistenceService.persistPlayers(players, "manual");
        return "✅ Loaded " + count + " players successfully!";
    }
}