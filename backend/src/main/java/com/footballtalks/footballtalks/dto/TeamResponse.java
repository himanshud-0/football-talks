package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {
    private Long id;
    private String name;
    private String country;
    private String league;
    private String logoUrl;
    private CompetitionSummaryResponse competition;
    private long playerCount;
    private long totalMarketValue;
}
