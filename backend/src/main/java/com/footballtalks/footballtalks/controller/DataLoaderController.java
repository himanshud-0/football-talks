package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.scheduler.PlayerSyncScheduler;
import com.footballtalks.footballtalks.service.ApiFootballPersistenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class DataLoaderController {

    private final ApiFootballPersistenceService persistenceService;
    private final PlayerSyncScheduler playerSyncScheduler;

    public DataLoaderController(ApiFootballPersistenceService persistenceService,
                                PlayerSyncScheduler playerSyncScheduler) {
        this.persistenceService = persistenceService;
        this.playerSyncScheduler = playerSyncScheduler;
    }

    @PostMapping("/load")
    public String loadPlayers(@RequestBody List<PlayerResponse> players) {
        int count = persistenceService.persistPlayers(players, "manual");
        return "✅ Loaded " + count + " players successfully!";
    }

    @PostMapping("/sync")
    public String syncNow() {
        playerSyncScheduler.syncNextLeague();
        return "✅ Sync triggered! Check Render logs for progress.";
    }
}
