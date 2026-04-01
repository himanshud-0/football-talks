package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private Long id;
    private Long playerId;
    private String playerName;
    private String playerImage;
    private Long playerMarketValue;
    private TeamSummaryResponse fromTeam;
    private TeamSummaryResponse toTeam;
    private Long transferFee;
    private String transferType;
    private String transferFeeFormatted;
    private LocalDate transferDate;
    private String season;
}
