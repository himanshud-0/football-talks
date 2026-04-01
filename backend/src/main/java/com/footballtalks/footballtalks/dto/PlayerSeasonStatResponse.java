package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSeasonStatResponse {
    private Long id;
    private String season;
    private CompetitionSummaryResponse competition;
    private TeamSummaryResponse team;
    private Integer appearances;
    private Integer goals;
    private Integer assists;
    private Integer minutes;
    private BigDecimal rating;
}
