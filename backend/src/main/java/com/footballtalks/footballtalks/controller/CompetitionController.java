package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.CompetitionResponse;
import com.footballtalks.footballtalks.service.TransferMarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final TransferMarketService transferMarketService;

    public CompetitionController(TransferMarketService transferMarketService) {
        this.transferMarketService = transferMarketService;
    }

    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> getCompetitions() {
        return ResponseEntity.ok(transferMarketService.getCompetitions());
    }
}
