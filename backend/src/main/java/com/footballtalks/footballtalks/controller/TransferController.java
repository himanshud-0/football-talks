package com.footballtalks.footballtalks.controller;

import com.footballtalks.footballtalks.dto.TransferResponse;
import com.footballtalks.footballtalks.service.ApiFootballTransferService;
import com.footballtalks.footballtalks.service.TransferMarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private final TransferMarketService transferMarketService;
    private final ApiFootballTransferService apiFootballTransferService;

    public TransferController(TransferMarketService transferMarketService,
                              ApiFootballTransferService apiFootballTransferService) {
        this.transferMarketService = transferMarketService;
        this.apiFootballTransferService = apiFootballTransferService;
    }

    @GetMapping
    public ResponseEntity<List<TransferResponse>> getTransfers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer league,
            @RequestParam(required = false) Integer apiSeason,
            @RequestParam(defaultValue = "false") boolean refresh) {
        if (apiFootballTransferService.isConfigured()) {
            try {
                return ResponseEntity.ok()
                        .header("X-Transfers-Source", "api-football")
                        .body(apiFootballTransferService.getTransfers(search, season, limit, league, apiSeason, refresh));
            } catch (RuntimeException exception) {
                log.warn("Falling back to local transfers after API-Football fetch failed", exception);
                return ResponseEntity.ok()
                        .header("X-Transfers-Source", "local-fallback")
                        .header("X-Transfers-Fallback-Reason", exception.getMessage())
                        .body(transferMarketService.getTransfers(search, season, limit));
            }
        }

        return ResponseEntity.ok()
                .header("X-Transfers-Source", "local")
                .body(transferMarketService.getTransfers(search, season, limit));
    }
}
