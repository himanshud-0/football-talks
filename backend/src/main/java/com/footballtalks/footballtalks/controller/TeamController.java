package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.TeamResponse;
import com.footballtalks.footballtalks.service.TransferMarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TransferMarketService transferMarketService;

    public TeamController(TransferMarketService transferMarketService) {
        this.transferMarketService = transferMarketService;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long competitionId,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(transferMarketService.getTeams(search, competitionId, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return transferMarketService.getTeams(null, null, null)
                .stream()
                .filter(t -> t.getId() != null && t.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
